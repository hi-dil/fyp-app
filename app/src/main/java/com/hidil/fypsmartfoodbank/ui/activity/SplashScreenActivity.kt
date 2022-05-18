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

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DatabaseRepo().getLocation(this)

        val currentUserID = AuthenticationRepo().getCurrentUserID()
        val sp = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val isBeneficiary = sp.getBoolean(Constants.IS_BENEFICIARY, false)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (hasLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                val spEditor = sp.edit()
                spEditor.putString(Constants.CURRENT_LAT, location.latitude.toString())
                spEditor.putString(Constants.CURRENT_LONG, location.longitude.toString())
                spEditor.apply()
            }
        } else {
            requestLocationPermission()
        }

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                if (isBeneficiary) {
                    val activeRequest = DatabaseRepo().getActiveRequestAsync()
                    val userDetails = DatabaseRepo().getUserDetailAsync()


                    if (currentUserID.isNotEmpty()) {
                        val intent = Intent(
                            this@SplashScreenActivity,
                            BeneficiaryMainActivity::class.java
                        )
                        intent.putExtra("activeRequest", activeRequest)
                        intent.putExtra("userDetails", userDetails)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                    }

                    finish()
                } else {
                    val activeRequest = DatabaseRepo().getActiveRequestDonatorAsync()
                    val userDetails = DatabaseRepo().getUserDetailAsync()

                    if (currentUserID.isNotEmpty()) {
                        val intent = Intent(
                            this@SplashScreenActivity,
                            DonatorActivity::class.java
                        )
                        intent.putExtra("activeRequest", activeRequest)
                        intent.putExtra("userDetails", userDetails)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                    }

                    finish()
                }
            }
        }


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
}