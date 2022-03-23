package com.hidil.fypsmartfoodbank.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivityLoginBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvForgotPassword.setOnClickListener {
            Intent(this, ForgotPassword::class.java).also {
                startActivity(it)
            }
        }

        binding.tvSignUp.setOnClickListener {
            Intent(this, SignUp::class.java).also {
                startActivity(it)
            }
        }

        binding.btnLogin.setOnClickListener {
            if(validateLoginDetails()) {
                showProgressDialog()
                val email = binding.etEmail.text.toString().trim{ it <= ' ' }
                val password = binding.etPassword.text.toString().trim{ it <= ' ' }
                AuthenticationRepo().userLogin(this, email, password)
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }

            else -> true
        }
    }
}