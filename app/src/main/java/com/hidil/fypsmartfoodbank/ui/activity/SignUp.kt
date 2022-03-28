package com.hidil.fypsmartfoodbank.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivitySignUpBinding
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.StorageRepo
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.io.IOException

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var mSelectedImageFileUri: Uri? = null
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
            showProgressDialog()
            if (validateRegisterDetails()) {
                val email = binding.etEmail.text.toString().trim { it <= ' ' }
                val password = binding.etPassword.text.toString().trim { it <= ' ' }
                val user = User(
                    "",
                    binding.acUserRole.text.toString().trim { it <= ' ' },
                    binding.etName.text.toString().trim { it <= ' ' },
                    binding.etEmail.text.toString().trim { it <= ' ' },
                    "",
                    binding.acMonthlyIncome.text.toString().trim { it <= ' ' },
                    binding.etPhoneNumber.text.toString().trim { it <= ' ' }
                )
                AuthenticationRepo().createUser(this, email, password, user)
            }
        }

        binding.ivImageProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivImageProfile
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.acUserRole.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_usr_role), true)
                false
            }

            TextUtils.isEmpty(binding.etName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_password), true)
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_phone), true)
                false
            }

            TextUtils.isEmpty(binding.acMonthlyIncome.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_monthlyIncome), true)
                false
            }

            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_password_not_match), true)
                false
            }

            else -> true
        }
    }
}