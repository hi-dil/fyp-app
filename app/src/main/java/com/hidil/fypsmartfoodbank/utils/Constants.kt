package com.hidil.fypsmartfoodbank.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment

object Constants {
    val CURRENT_LONG: String = "current_long"
    val CURRENT_LAT: String = "current_lat"
    val USER_CITY = "city"
    val USER_STATE = "state"
    const val USERS: String = "users"

    const val APP_PREF: String = "AppPrefs"
    const val LOGGED_IN_USER: String = "LoggedInUser"
    const val EXTRA_USER_DETAILS: String = "ExtraUserDetails"
    const val LOCATION_ARRAYLIST: String = "LocationArrayList"

    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val LOCATION_REQUEST_CODE = 3
    const val CAMERA_REQUEST_CODE = 4

    val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    val ITEM_IMAGE: String = "Item_Image"

//    Firebase database field names
    const val NAME: String = "name"
    const val USER_ROLE: String = "userRole"
    const val EMAIL: String = "email"
    const val USER_IMAGE: String = "image"
    const val MONTHLY_INCOME: String = "monthlyIncome"
    const val MOBILE_NUMBER: String = "mobileNumber"
    const val IS_BENEFICIARY: String = "isBeneficiary"

    const val USER_ID: String = "userID"
    const val REQUEST: String = "request"
    const val DONATION_REQUEST: String = "donationRequest"
    const val FOODBANK: String = "foodBank"
    const val STORAGE: String = "foodBankStorage"
    const val REQUEST_COMPLETE: String = "completed"
    const val REQUEST_APPROVED: String = "approved"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI )

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun chooseMultipleImages(fragment: Fragment) {
        // For latest versions API LEVEL 19+
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        fragment.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}