package com.hidil.fypsmartfoodbank.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivitySplashScreenBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUserID = AuthenticationRepo().getCurrentUserID()

        if (currentUserID.isNotEmpty()) {
            startActivity(Intent(this, BeneficiaryMainActivity::class.java))
        } else {
            startActivity(Intent(this, Login::class.java))
        }

        finish()
    }
}