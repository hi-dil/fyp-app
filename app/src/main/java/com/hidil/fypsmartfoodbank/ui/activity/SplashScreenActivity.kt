package com.hidil.fypsmartfoodbank.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
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

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DatabaseRepo().getLocation(this)

        val currentUserID = AuthenticationRepo().getCurrentUserID()
        val sp = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val userRole = sp.getString(Constants.USER_ROLE, "")

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                if (userRole != null && userRole.isNotEmpty()) {
                    // get all the food bank location and pass the data to the next page through intent
                    val locationList = DatabaseRepo().getLocationAsync()

                    val sharedPreferences = getSharedPreferences(
                        Constants.APP_PREF,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    val location = Gson().toJson(locationList)
                    editor.putString(Constants.LOCATION_ARRAYLIST, location)
                    editor.apply()

                    // check user's role and navigate user's to the appropriate page
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
                            finish()
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
                            finish()
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
                            finish()
                        }
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                    }
                } else {
                    startActivity(Intent(this@SplashScreenActivity, Login::class.java))
                }
            }
        }


    }

    // request for location permission from user
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