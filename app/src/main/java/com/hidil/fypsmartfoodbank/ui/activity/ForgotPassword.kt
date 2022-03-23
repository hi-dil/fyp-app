package com.hidil.fypsmartfoodbank.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hidil.fypsmartfoodbank.databinding.ActivityForgotPasswordBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo

class ForgotPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim { it <= ' '}
            if (email.isEmpty()) {
                showErrorSnackBar("Please enter your email address", true)
            } else {
                showProgressDialog()
                AuthenticationRepo().forgotPassword(this, email)
            }
        }
    }
}