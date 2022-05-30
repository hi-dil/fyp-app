package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.hidil.fypsmartfoodbank.model.*
import com.hidil.fypsmartfoodbank.ui.activity.EditProfileActivity
import com.hidil.fypsmartfoodbank.ui.activity.Login
import com.hidil.fypsmartfoodbank.ui.activity.SignUp
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.UserInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ConfirmRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.FoodBankInfoFragment
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

                editor.putString(
                    Constants.MOBILE_NUMBER,
                    user.mobileNumber
                )

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
                    is ConfirmRequestFragment -> fragment.assignActiveRequest(activeRequestList)
                    is UserInfoFragment -> fragment.getActiveRequest(activeRequestList)
                }
            }
    }

    suspend fun getActiveRequestAsync(): ArrayList<Request> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.REQUEST)
                .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .whereEqualTo(Constants.IS_DENIED, false)
                .whereEqualTo(Constants.IS_CANCEL, false)
                .orderBy(Constants.REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()

            val activeRequest: ArrayList<Request> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(Request::class.java)
                request!!.id = i.id
                activeRequest.add(request)
            }
            return@withContext activeRequest
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

    suspend fun getPastRequestAsync(): ArrayList<Request> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.REQUEST)
                .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
                .whereEqualTo(Constants.REQUEST_COMPLETE, true)
                .orderBy(Constants.REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()

            val activeRequest: ArrayList<Request> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(Request::class.java)
                request!!.id = i.id
                activeRequest.add(request)
            }
            return@withContext activeRequest
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

    suspend fun getLocationAsync(): ArrayList<Location> {
        val dataChunk: ArrayList<DataChunk> = ArrayList()
        return withContext(Dispatchers.IO) {
            val query = mFirestore.collection("dataChunk")
                .get()
                .await()

            for (i in query.documents) {
                val data = i.toObject(DataChunk::class.java)
                dataChunk.add(data!!)
            }

            return@withContext dataChunk[0].location
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
                if (foodBankRequest.size > 0) {
                    fragment.getFoodBankData(foodBankRequest[0])
                }
            }
    }

    suspend fun searchFoodBank(fragment: Fragment, id: String): ArrayList<FoodBank> {
        return withContext(Dispatchers.IO) {
            val foodbankList: ArrayList<FoodBank> = ArrayList()
            mFirestore.collection(Constants.FOODBANK)
                .whereEqualTo(FieldPath.documentId(), id)
                .get()
                .addOnSuccessListener { document ->
                    for (i in document.documents) {
                        val data = i.toObject(FoodBank::class.java)
                        data!!.id = i.id
                        foodbankList.add(data)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(fragment.javaClass.simpleName.toString(), e.message.toString())
                }
                .await()

            return@withContext foodbankList
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
                fragment.getStorageData(storageList[0])
            }
    }

    suspend fun searchRequestAsync(fragment: Fragment, id: String): ArrayList<Request> {
        return withContext(Dispatchers.IO) {
            val requestList: ArrayList<Request> = ArrayList()
            mFirestore.collection(Constants.REQUEST)
                .whereEqualTo(FieldPath.documentId(), id)
                .get()
                .addOnSuccessListener { document ->
                    for (i in document.documents) {
                        val request = i.toObject(Request::class.java)
                        request!!.id = i.id
                        requestList.add(request)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(fragment.javaClass.simpleName.toString(), e.message.toString())
                }
                .await()

            return@withContext requestList
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

    suspend fun updateFoodBank(fragment: Fragment, foodBank: FoodBank): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false

            mFirestore.collection(Constants.FOODBANK)
                .document(foodBank.id)
                .set(foodBank)
                .addOnSuccessListener {
                    success = true
                    Log.i("testfirst", "in")
                }
                .addOnFailureListener {
                    Log.e(fragment.javaClass.simpleName.toString(), it.message.toString())
                }
                .await()
            Log.i("testfirst", success.toString())

            return@withContext success
        }
    }

    suspend fun saveRequest(fragment: Fragment, request: Request): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false
            mFirestore.collection(Constants.REQUEST)
                .document()
                .set(request, SetOptions.merge())
                .addOnSuccessListener {
                    success = true
                    Log.i("test", "in")
                }
                .addOnFailureListener {
                    Log.e(fragment.javaClass.simpleName.toString(), it.message.toString())
                }
                .await()

            return@withContext success
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

    suspend fun getActiveRequestDonatorAsync(): ArrayList<DonationRequest> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.DONATION_REQUEST)
                .whereEqualTo(Constants.USER_ID, AuthenticationRepo().getCurrentUserID())
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .whereEqualTo(Constants.IS_DENIED, false)
                .whereEqualTo(Constants.IS_CANCEL, false)
                .orderBy(Constants.REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()


            val activeRequest: ArrayList<DonationRequest> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(DonationRequest::class.java)
                request!!.id = i.id
                activeRequest.add(request)
            }

            return@withContext activeRequest


        }
    }

    suspend fun getOldCompleteDonationRequest(fragment: Fragment): ArrayList<DonationRequest> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.DONATION_REQUEST)
                .whereEqualTo(Constants.REQUEST_COMPLETE, true)
                .orderBy("requestDate", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()

            val donationRequest: ArrayList<DonationRequest> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(DonationRequest::class.java)
                request!!.id = i.id
                donationRequest.add(request)
            }

            return@withContext donationRequest
        }

    }

    suspend fun saveRequestDonation(request: DonationRequest, fragment: Fragment): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false

            mFirestore.collection(Constants.DONATION_REQUEST)
                .document()
                .set(request, SetOptions.merge())
                .addOnSuccessListener { success = true }
                .addOnFailureListener {
                    Log.e(
                        fragment.javaClass.simpleName.toString(),
                        it.message.toString()
                    )
                }
                .await()

            return@withContext success
        }
    }


    fun updateUserProfile(fragment: Fragment, user: User) {
        mFirestore.collection(Constants.USERS)
            .document(AuthenticationRepo().getCurrentUserID())
            .set(user)
            .addOnSuccessListener {
                Log.i("test", "success update profile")
            }
    }

    // admin

    suspend fun getOldestClaimRequest(count: Long): ArrayList<Request> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.REQUEST)
                .whereEqualTo(Constants.REQUEST_APPROVED, false)
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .orderBy("requestDate", Query.Direction.DESCENDING)
                .limit(count)
                .get()
                .await()

            val claimRequest: ArrayList<Request> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(Request::class.java)
                request!!.id = i.id
                claimRequest.add(request)
            }

            return@withContext claimRequest
        }
    }

    suspend fun getOldDonationRequest(count: Long): ArrayList<DonationRequest> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = mFirestore.collection(Constants.DONATION_REQUEST)
                .whereEqualTo(Constants.REQUEST_APPROVED, false)
                .whereEqualTo(Constants.REQUEST_COMPLETE, false)
                .orderBy("requestDate", Query.Direction.DESCENDING)
                .limit(count)
                .get()
                .await()

            val donationRequest: ArrayList<DonationRequest> = ArrayList()
            for (i in querySnapshot.documents) {
                val request = i.toObject(DonationRequest::class.java)
                request!!.id = i.id
                donationRequest.add(request)
            }

            return@withContext donationRequest
        }
    }

    suspend fun updateUserDonationRequest(
        currentRequest: DonationRequest,
        fragment: Fragment
    ): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false
            mFirestore.collection(Constants.DONATION_REQUEST)
                .document(currentRequest.id)
                .set(currentRequest)
                .addOnSuccessListener { success = true }
                .addOnFailureListener {
                    Log.e(
                        fragment.javaClass.simpleName.toString(),
                        it.message.toString()
                    )
                }
                .await()

            return@withContext success
        }
    }

    suspend fun updateUserClaimRequest(
        currentRequest: Request,
        fragment: Fragment
    ): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false
            mFirestore.collection(Constants.REQUEST)
                .document(currentRequest.id)
                .set(currentRequest)
                .addOnSuccessListener { success = true }
                .addOnFailureListener {
                    Log.e(
                        fragment.javaClass.simpleName.toString(),
                        it.message.toString()
                    )
                }
                .await()

            return@withContext success
        }
    }
}