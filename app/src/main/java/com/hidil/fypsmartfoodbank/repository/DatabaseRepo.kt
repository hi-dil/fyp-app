package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.hidil.fypsmartfoodbank.model.*
import com.hidil.fypsmartfoodbank.ui.activity.EditProfileActivity
import com.hidil.fypsmartfoodbank.ui.activity.Login
import com.hidil.fypsmartfoodbank.ui.activity.SignUp
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.fragments.FoodBankInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.UserInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestDetailsFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ConfirmRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragment
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DashboardDonatorFragment
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DonationRequestFragment
import com.hidil.fypsmartfoodbank.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DatabaseRepo {
    private val mFirestore = FirebaseFirestore.getInstance()

    //  ------------- Claim Request --------------
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

                if (user.userRole.lowercase() == "beneficiary") {
                    editor.putBoolean(Constants.IS_BENEFICIARY, true)
                } else {
                    editor.putBoolean(Constants.IS_BENEFICIARY, false)
                }

                editor.putString(Constants.USER_STATE, user.state)
                editor.putString(Constants.USER_ROLE, user.userRole)
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

    suspend fun getUserDetailAsync(): User {
        return withContext(Dispatchers.IO) {
            mFirestore.collection(Constants.USERS)
                .document(AuthenticationRepo().getCurrentUserID())
                .get()
                .await()
                .toObject(User::class.java)!!
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

    fun getActiveRequest(fragment: Fragment, id: String) {
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(Constants.USER_ID, id)
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
                    is ClaimRequestFragment -> fragment.successRequestFromFirestore(
                        activeRequestList
                    )
                    is ConfirmRequestFragment -> fragment.assignActiveRequest(activeRequestList)
                    is UserInfoFragment -> fragment.getActiveRequest(activeRequestList)
                }
            }
    }

    suspend fun getActiveRequestAsync(): Request {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.REQUEST)
                .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .get()
                .await()

            val activeRequest: ArrayList<Request> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(Request::class.java)
                request!!.id = i.id
                activeRequest.add(request)
            }
            return@withContext activeRequest[0]
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

    fun getPastRequest(fragment: Fragment, id: String) {
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(Constants.USER_ID, id)
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
                    is UserInfoFragment -> fragment.getPastRequest(pastRequestList)
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting past request " + e.message.toString()
                )
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

    fun getLocation(activity: Activity) {
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

    fun searchFoodBankDetails(fragment: FoodBankInfoFragment, id: String) {
        Log.i("test", id)
        mFirestore.collection(Constants.FOODBANK)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .addOnSuccessListener { document ->
                val foodBankRequest: ArrayList<FoodBank> = ArrayList()
                for (i in document.documents) {
                    val data = i.toObject(FoodBank::class.java)
                    data!!.id = i.id

                    foodBankRequest.add(data)
                }
                if (foodBankRequest.size> 0) {
                    fragment.getFoodBankData(foodBankRequest[0])
                }
            }

    }

    fun searchStorageDetails(fragment: StorageInfoFragment, id: String) {
        mFirestore.collection(Constants.STORAGE)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { document ->
                val storageList: ArrayList<Storage> = ArrayList()
                for (i in document.documents) {
                    val data = i.toObject(Storage::class.java)
                    data!!.id = i.id
                    storageList.add(data)
                }
                Log.i("storage", storageList.toString())
                fragment.getStorageData(storageList[0])
            }
    }

    fun searchRequest(fragment: ClaimRequestDetailsFragment, id: String) {
        mFirestore.collection(Constants.REQUEST)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .addOnSuccessListener { document ->
                val requestList: ArrayList<Request> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(Request::class.java)
                    request!!.id = i.id

                    requestList.add(request)
                }

                fragment.refreshList(requestList[0].items)

            }
    }

    fun searchUser(fragment: UserInfoFragment, id: String) {
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .addOnSuccessListener { document ->
                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    val user = i.toObject(User::class.java)
                    user!!.id = i.id

                    usersList.add(user)
                }
                fragment.getUserInfo(usersList[0])
            }
    }

    fun saveRequest(fragment: Fragment, request: Request) {
        mFirestore.collection(Constants.REQUEST)
            .document()
            .set(request, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(
                    fragment.requireContext(),
                    "You have successfully save the data to the firestore",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Log.e(fragment.javaClass.simpleName, "Error while uploading file to database")
            }
    }


    // ------------ Donation Request --------------
    fun getDonationActiveRequest(fragment: Fragment, id: String) {
        mFirestore.collection(Constants.DONATION_REQUEST)
            .whereEqualTo(Constants.USER_ID, id)
            .whereEqualTo(Constants.REQUEST_COMPLETE, false)
            .get()
            .addOnSuccessListener { document ->
                val activeRequestList: ArrayList<DonationRequest> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(DonationRequest::class.java)
                    request!!.id = i.id

                    activeRequestList.add(request)
                }

                when (fragment) {
//                    is DashboardDonatorFragment -> fragment.getActiveRequest(activeRequestList)
                    is DonationRequestFragment -> fragment.getActiveRequest(activeRequestList)
                }
            }
    }

    suspend fun getActiveRequestDonatorAsync(): DonationRequest {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.DONATION_REQUEST)
                .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .get()
                .await()



            val activeRequest: ArrayList<DonationRequest> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(DonationRequest::class.java)
                request!!.id = i.id
                activeRequest.add(request)
            }

            return@withContext activeRequest[0]
        }
    }

    fun saveRequestDonation(fragment: Fragment, request: DonationRequest): Boolean {
        var success = false
        mFirestore.collection(Constants.DONATION_REQUEST)
            .document()
            .set(request, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(
                    fragment.requireContext(),
                    "Successfully save the request to the database",
                    Toast.LENGTH_SHORT
                ).show()
                success = true
            }
            .addOnFailureListener {
                Toast.makeText(
                    fragment.requireContext(),
                    "Error while saving request",
                    Toast.LENGTH_SHORT
                ).show()
            }

        return success
    }

    fun getPastDonationRequest(fragment: Fragment, id: String) {
        mFirestore.collection(Constants.DONATION_REQUEST)
            .whereEqualTo(Constants.USER_ID, id)
            .whereEqualTo(Constants.REQUEST_COMPLETE, true)
            .get()
            .addOnSuccessListener { document ->
                val pastRequestList: ArrayList<DonationRequest> = ArrayList()
                for (i in document.documents) {
                    val request = i.toObject(DonationRequest::class.java)
                    request!!.id = i.id
                    pastRequestList.add(request)
                }

                when (fragment) {
                    is DonationRequestFragment -> fragment.getPastRequest(pastRequestList)
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting past request " + e.message.toString()
                )
            }

    }

    fun updateUserProfile(fragment: Fragment, user: User) {
        Log.i("test", "sending data")
        mFirestore.collection(Constants.USERS)
            .document(AuthenticationRepo().getCurrentUserID())
            .set(user)
            .addOnSuccessListener {
                Log.i("test", "success update profile")
            }
    }

}