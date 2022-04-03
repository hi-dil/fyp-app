package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.ui.activity.*
import com.hidil.fypsmartfoodbank.utils.Constants

class DatabaseRepo {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUp, user: User) {
        mFirestore.collection(Constants.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.hideProgressDialog()
                Toast.makeText(
                    activity,
                    "You have successfully register to the app",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user.", it)
            }
    }

    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(AuthenticationRepo().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.APP_PREF,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USER,
                    user.name
                )

                editor.putString(
                    Constants.USER_PROFILE_IMAGE,
                    user.image
                )

                editor.putString(
                    Constants.USER_CITY,
                    user.city
                )

                editor.putString(Constants.USER_STATE, user.state)
                editor.apply()


                when (activity) {
                    is Login -> {
                        activity.loginSuccessful(user)
                    }

                    is EditProfileActivity -> {
                        activity.userDetailsSuccess(user)
                    }

                    is EditProfileActivity -> activity.userDetailsSuccess(user)
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is Login -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserProfileData(activity: EditProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.USERS)
            .document(AuthenticationRepo().getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                getUserDetails(activity)
                activity.userProfileUpdateSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating the user details")
            }
    }
}