package com.hidil.fypsmartfoodbank.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivityLoginBinding
import com.hidil.fypsmartfoodbank.model.User
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
            if (validateLoginDetails()) {
                showProgressDialog()
                val email = binding.etEmail.text.toString().trim { it <= ' ' }
                val password = binding.etPassword.text.toString().trim { it <= ' ' }
                AuthenticationRepo().userLogin(this, email, password)
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }

            else -> true
        }
    }

    fun loginSuccessful(user: User) {
        hideProgressDialog()
//        val intent = if (user.userRole == "Beneficiary") {
//            Intent(this, BeneficiaryMainActivity::class.java)
//        } else {
//            Intent(this, DonatorActivity::class.java)
//        }

        if (user.ban) {
            Toast.makeText(this, "Your account has been ban!", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        } else if (!user.ban){
            if (user.userRole.lowercase() == "beneficiary" || user.userRole.lowercase() == "donator" || user.userRole.lowercase() == "admin") {
                val intent = Intent(this, SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

//        val intent = if (user.userRole.lowercase() == "beneficiary" || user.userRole.lowercase() == "donator") {
//            Intent(this, SplashScreenActivity::class.java)
//        }
//       intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
//        startActivity(intent)
//        finish()
    }
}