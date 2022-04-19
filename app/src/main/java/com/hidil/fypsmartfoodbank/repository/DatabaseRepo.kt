package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.hidil.fypsmartfoodbank.model.*
import com.hidil.fypsmartfoodbank.ui.activity.*
import com.hidil.fypsmartfoodbank.ui.fragments.FoodBankInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.LocationFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestDetailsFragment
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

                val favouriteFoodBank = userDetails[0].favouriteFoodBank


                when (fragment) {
                    is DashboardFragment -> fragment.successGetFavouriteFoodBank(favouriteFoodBank)
                }
            }
    }

    fun getPastRequest(fragment: Fragment){
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
            .whereEqualTo(Constants.REQUEST_COMPLETE, true)
            .get()
            .addOnSuccessListener { document ->
                Log.i("Past request", document.documents.toString())
                val pastRequestList: ArrayList<Request> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(Request::class.java)
                    request!!.id = i.id

                    pastRequestList.add(request)
                }

                when (fragment) {
                    is ClaimRequestFragment -> {
                        fragment.successGetPastRequest(pastRequestList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error while getting past request " + e.message.toString())
            }
    }

    fun getFoodBankDetails(fragment: Fragment) {
        mFirestore.collection(Constants.FOODBANK)
            .get()
            .addOnSuccessListener { document ->
                val foodBankDetails: ArrayList<FoodBank> = ArrayList()

                for (i in document.documents) {
                    val details = i.toObject(FoodBank::class.java)
                    details!!.id = i.id

                    foodBankDetails.add(details)
                }

            }
    }

    fun getLocation(activity: Activity){
        val dataChunk: ArrayList<DataChunk> = ArrayList()
        mFirestore.collection("dataChunk")
            .get()
            .addOnSuccessListener { document ->

                for (i in document.documents) {
                    val data = i.toObject(DataChunk::class.java)
                    dataChunk.add(data!!)
                }
//                fragment.successLocation(dataChunk[0].location)

                Log.i("datachunk", dataChunk[0].location.toString())
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.APP_PREF,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val location = Gson().toJson(dataChunk[0].location)
                editor.putString(Constants.LOCATION_ARRAYLIST, location)
                editor.apply()
            }
    }

    fun getFoodBankDetails(fragment: FoodBankInfoFragment, id: String) {
        mFirestore.collection(Constants.FOODBANK)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { document ->
                val foodBankRequest: ArrayList<FoodBank> = ArrayList()
                for (i in document) {
                    val data = i.toObject(FoodBank::class.java)
                }

            }

    }

    fun searchRequest(fragment: ClaimRequestDetailsFragment, id: String) {
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .addOnSuccessListener { document ->
                val requestList: ArrayList<Request> = ArrayList()
                Log.i("test", id)
                Log.i("test", document.documents.toString())
                for (i in document.documents) {
                    val request = i.toObject(Request::class.java)
                    request!!.id = i.id

                    requestList.add(request)
                }

                fragment.refreshList(requestList[0].items)

            }
    }
}