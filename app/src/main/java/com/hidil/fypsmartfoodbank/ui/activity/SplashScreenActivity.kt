package com.hidil.fypsmartfoodbank.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hidil.fypsmartfoodbank.databinding.ActivitySplashScreenBinding
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.utils.Constants
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DatabaseRepo().getLocation(this)

        val currentUserID = AuthenticationRepo().getCurrentUserID()
        val sp = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val userRole = sp.getString(Constants.USER_ROLE, "")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (hasLocationPermission()) {

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val spEditor = sp.edit()
                    spEditor.putString(Constants.CURRENT_LAT, location.latitude.toString())
                    spEditor.putString(Constants.CURRENT_LONG, location.longitude.toString())
                    spEditor.apply()

                    currentLat = location.latitude
                    currentLong = location.longitude
                }
            }
        } else {
            requestLocationPermission()
        }

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                if (userRole != null && userRole.isNotEmpty()) {
                    val location = DatabaseRepo().getLocationAsync()
                    val sortList = sortLocation(location)
                    val locationList = ArrayList(sortList)

                    if (userRole.lowercase() == "beneficiary") {
                        val activeRequest = DatabaseRepo().getActiveRequestAsync()
                        val userDetails = DatabaseRepo().getUserDetailAsync()

                        if (currentUserID.isNotEmpty()) {
                            val intent = Intent(
                                this@SplashScreenActivity,
                                BeneficiaryMainActivity::class.java
                            )
                            if (activeRequest.size > 0) {
                                intent.putExtra("activeRequest", activeRequest)
                            }
                            intent.putExtra("userDetails", userDetails)
                            intent.putExtra("locationList", locationList)
                            startActivity(intent)
                        }

                        finish()
                    } else if (userRole.lowercase() == "donator") {
                        val activeRequest = DatabaseRepo().getActiveRequestDonatorAsync()
                        val userDetails = DatabaseRepo().getUserDetailAsync()

                        if (currentUserID.isNotEmpty()) {
                            val intent = Intent(
                                this@SplashScreenActivity,
                                DonatorActivity::class.java
                            )
                            intent.putExtra("activeRequest", activeRequest)
                            intent.putExtra("userDetails", userDetails)
                            intent.putExtra("locationList", locationList)
                            startActivity(intent)
                        }

                        finish()
                    } else if (userRole.lowercase() == "admin") {
                        val oldestDonationRequest = DatabaseRepo().getOldDonationRequest(3)
                        val oldestClaimRequest = DatabaseRepo().getOldestClaimRequest(3)

                        if (currentUserID.isNotEmpty()) {
                            val intent =
                                Intent(this@SplashScreenActivity, AdminMainActivity::class.java)
                            intent.putExtra("claimRequest", oldestClaimRequest)
                            intent.putExtra("donationRequest", oldestDonationRequest)
                            startActivity(intent)
                        }
                        finish()
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                    }
                } else {
                    startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                }
            }
        }


    }

    private fun sortLocation(list: ArrayList<Location>): List<Location> {
        for (i in list) {
            val distance =
                distanceInKm(currentLat, currentLong, i.lat.toDouble(), i.long.toDouble())
            i.distance = distance
        }

        return list.sortedWith(compareBy { it.distance })
    }

    private fun hasLocationPermission() = (
            EasyPermissions.hasPermissions(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this, "This application cannot work without location permission",
            Constants.LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
    }

    private fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}