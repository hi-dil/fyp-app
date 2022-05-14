package com.hidil.fypsmartfoodbank.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivitySplashScreenBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.utils.Constants

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DatabaseRepo().getLocation(this)

        val currentUserID = AuthenticationRepo().getCurrentUserID()

        if (currentUserID.isNotEmpty()) {
            val sp = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
            val userRole = sp?.getString(Constants.USER_ROLE, "")!!

            if (userRole == "Beneficiary") {
                startActivity(Intent(this, BeneficiaryMainActivity::class.java))
            } else {
                startActivity(Intent(this, DonatorActivity::class.java))
            }
        } else {
            startActivity(Intent(this, Login::class.java))
        }

        finish()
    }
}