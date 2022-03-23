package com.hidil.fypsmartfoodbank.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivitySignUpBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRole = resources.getStringArray(R.array.user_role)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_userrole_item, userRole)
        binding.acUserRole.setAdapter(arrayAdapter)

        val monthlyIncome = resources.getStringArray(R.array.monthly_income)
        val arrayAdapterIncome = ArrayAdapter(this, R.layout.dropdown_userrole_item, monthlyIncome)
        binding.acMonthlyIncome.setAdapter(arrayAdapterIncome)

        binding.fabBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            if(validateRegisterDetails()) {
                showProgressDialog()
                val email = binding.etEmail.text.toString().trim { it <= ' ' }
                val password = binding.etPassword.text.toString().trim { it <= ' ' }
                AuthenticationRepo().createUser(this, email, password)
            }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.acUserRole.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_usr_role), true)
                false
            }

            TextUtils.isEmpty(binding.etName.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_name), true)
                false
            }

            TextUtils.isEmpty(binding.etUserName.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_userName), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_password), true)
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_phone), true)
                false
            }

            TextUtils.isEmpty(binding.acMonthlyIncome.text.toString().trim{ it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_monthlyIncome), true)
                false
            }

            binding.etPassword.text.toString().trim { it <= ' ' } != binding.etConfirmPassword.text.toString().trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_password_not_match), true)
                false
            }

            else -> true
        }
    }
}