package com.hidil.fypsmartfoodbank.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivitySignUpBinding
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
//    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create dropdown for user role
        val userRole = resources.getStringArray(R.array.user_role)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_userrole_item, userRole)
        binding.acUserRole.setAdapter(arrayAdapter)

        // create dropdown for monthly income
        val monthlyIncome = resources.getStringArray(R.array.monthly_income)
        val arrayAdapterIncome = ArrayAdapter(this, R.layout.dropdown_userrole_item, monthlyIncome)
        binding.acMonthlyIncome.setAdapter(arrayAdapterIncome)

        binding.fabBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            showProgressDialog()
            registerUser()
        }

//        binding.ivImageProfile.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                Constants.showImageChooser(this)
//            } else {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    Constants.READ_STORAGE_PERMISSION_CODE
//                )
//            }
//        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Constants.showImageChooser(this)
//        } else {
//            Toast.makeText(
//                this,
//                resources.getString(R.string.read_storage_permission_denied),
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
//                if (data != null) {
//                    try {
//                        mSelectedImageFileUri = data.data!!
//                        GlideLoader(this).loadUserPicture(
//                            mSelectedImageFileUri!!,
//                            binding.ivImageProfile
//                        )
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                        Toast.makeText(
//                            this,
//                            resources.getString(R.string.image_selection_failed),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//    }

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

            TextUtils.isEmpty(binding.etState.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_state), true)
                false
            }

            TextUtils.isEmpty(binding.etCity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_city), true)
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

    // register the user if all of the input has been validate by the app
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val uid = AuthenticationRepo().createUserAsync(email, password)
                    Log.i("userid", uid)

                    var token = ""
                    if (uid.isNotEmpty()) {

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            token = task.result
                            Log.i("tokenfcm", token)
                        }.await()

                        val user = User(
                            uid,
                            binding.acUserRole.text.toString().trim { it <= ' ' },
                            binding.etName.text.toString().trim { it <= ' ' },
                            binding.etEmail.text.toString().trim { it <= ' ' },
                            "https://firebasestorage.googleapis.com/v0/b/smart-foodbank.appspot.com/o/def_profile.jpg?alt=media&token=61ba0a89-3dec-400e-94d3-bbbb490531e2",
                            binding.acMonthlyIncome.text.toString().trim { it <= ' ' },
                            binding.etPhoneNumber.text.toString().trim { it <= ' ' },
                            binding.etState.text.toString().trim { it <= ' ' },
                            binding.etCity.text.toString().trim { it <= ' ' },
                            token
                        )

                        val saveUserData = DatabaseRepo().regiserUserAsync(user)
                        this@SignUp.runOnUiThread {
                            if (saveUserData) {
                                hideProgressDialog()
                                Toast.makeText(
                                    this@SignUp,
                                    "You have successfully register to the app",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                hideProgressDialog()
                                Toast.makeText(
                                    this@SignUp,
                                    "There was an error while saving the data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    FirebaseAuth.getInstance().signOut()
                }
            }
        }
    }
}