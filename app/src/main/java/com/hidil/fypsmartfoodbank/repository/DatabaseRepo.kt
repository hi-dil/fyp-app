package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.ui.activity.*
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragment
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

    fun getActiveRequest(fragment: Fragment){
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
            .whereEqualTo(Constants.REQUEST_COMPLETE, false)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Active request", document.documents.toString())
                val activeRequestList: ArrayList<Request> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(Request::class.java)
                    request!!.id = i.id

                    activeRequestList.add(request)
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successRequestFromFirestore(activeRequestList)
                    }
                    is ClaimRequestFragment -> fragment.successRequestFromFirestore(activeRequestList)
                }
            }
    }

    fun getFavouriteFoodBank(fragment: Fragment) {
        mFirestore.collection(Constants.USERS)
            .whereEqualTo("id", AuthenticationRepo().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Favourite FoodBank", document.documents.toString())
                val userDetails: ArrayList<User> = ArrayList()
                for (i in document.documents) {
                    val user = i.toObject(User::class.java)
                    user!!.id = i.id

                    userDetails.add(user)
                }

                Log.i("Favourite Foodbank", userDetails[0].favouriteFoodBank.toString())
                val favouriteFoodBank = userDetails[0].favouriteFoodBank


                when (fragment) {
                    is DashboardFragment -> fragment.successGetFavouriteFoodBank(favouriteFoodBank)
                }
            }
    }

    fun getCompletedRequest(fragment: Fragment){
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
            .whereEqualTo(Constants.REQUEST_COMPLETE, true)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Active request", document.documents.toString())
                val activeRequestList: ArrayList<Request> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(Request::class.java)
                    request!!.id = i.id

                    activeRequestList.add(request)
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successRequestFromFirestore(activeRequestList)
                    }
                }
            }
    }
}