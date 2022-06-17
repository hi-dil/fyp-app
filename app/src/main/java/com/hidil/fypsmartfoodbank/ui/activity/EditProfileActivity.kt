@file:Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration",
    "OverrideDeprecatedMigration", "OverrideDeprecatedMigration", "OverrideDeprecatedMigration"
)

package com.hidil.fypsmartfoodbank.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.hidil.fypsmartfoodbank.databinding.ActivityEditProfileBinding
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.StorageRepo
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserDetails()

        val userRole = resources.getStringArray(R.array.user_role)
        val roleArrayAdapter = ArrayAdapter(this, R.layout.dropdown_userrole_item, userRole)
        binding.acUserRole.setAdapter(roleArrayAdapter)

        val monthlyIncome = resources.getStringArray(R.array.monthly_income)
        val incomeArrayAdapter = ArrayAdapter(this, R.layout.dropdown_userrole_item, monthlyIncome)
        binding.acMonthlyIncome.setAdapter(incomeArrayAdapter)

        binding.fabBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivImageProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnSave.setOnClickListener {
            if (validateRegisterDetails()) {
                showProgressDialog()

                if (mSelectedImageFileUri != null) {
                    StorageRepo().uploadImageToCloudStorage(
                        this,
                        mSelectedImageFileUri,
                        Constants.USER_PROFILE_IMAGE
                    )
                } else {
                    updateUserProfileDetails()
                }
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
                        Toast.makeText(this, "image selection successful", Toast.LENGTH_SHORT)
                            .show()
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

    private fun getUserDetails() {
        showProgressDialog()
        DatabaseRepo().getUserDetails(this)
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val name = binding.etName.text.toString().trim { it <= ' ' }
        if (name != mUserDetails.name) {
            userHashMap[Constants.NAME] = name
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.USER_IMAGE] = mUserProfileImageURL
        }

        val userRole = binding.acUserRole.text.toString().trim { it <= ' ' }
        if (userRole != mUserDetails.userRole) {
            userHashMap[Constants.USER_ROLE] = userRole
        }

        val phoneNumber = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        if (phoneNumber != mUserDetails.mobileNumber) {
           userHashMap[Constants.MOBILE_NUMBER] = phoneNumber
        }

        val monthlyIncome = binding.acMonthlyIncome.text.toString().trim { it <= ' ' }
        if (monthlyIncome != mUserDetails.monthlyIncome) {
            userHashMap[Constants.MONTHLY_INCOME] = monthlyIncome
        }

        DatabaseRepo().updateUserProfileData(this, userHashMap)
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

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_phone), true)
                false
            }

            TextUtils.isEmpty(binding.acMonthlyIncome.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_monthlyIncome), true)
                false
            }

            TextUtils.isEmpty(binding.etState.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_state), true)
                false
            }

            TextUtils.isEmpty(binding.etCity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_city), true)
                false
            }

            else -> true
        }
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        hideProgressDialog()

        GlideLoader(this).loadUserPicture(user.image, binding.ivImageProfile)
        binding.etName.setText(user.name)
        binding.acUserRole.setText(user.userRole)
        binding.etEmail.setText(user.email)
        binding.etPhoneNumber.setText(user.mobileNumber)
//        binding.acMonthlyIncome.setText(user.monthlyIncome)
        binding.etState.setText(user.state)
        binding.etCity.setText(user.city)

        binding.etEmail.isEnabled = false
        binding.acUserRole.isEnabled = false
        binding.etName.isEnabled = false
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    @SuppressLint("CommitPrefEdits")
    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this, "Your profile is updated successfully",
            Toast.LENGTH_SHORT
        ).show()

//        val editSP = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE).edit()
//        editSP.putString(Constants.LOGGED_IN_USER, user[Constants.NAME].toString())
//        editSP.putString(Constants.USER_PROFILE_IMAGE, user[Constants.USER_IMAGE].toString())
//        editSP.apply()
    }
}