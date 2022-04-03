package com.hidil.fypsmartfoodbank.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    val USER_CITY = "city"
    val USER_STATE = "state"
    const val USERS: String = "users"

    const val APP_PREF: String = "AppPrefs"
    const val LOGGED_IN_USER: String = "LoggedInUser"
    const val EXTRA_USER_DETAILS: String = "ExtraUserDetails"

    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    val USER_PROFILE_IMAGE: String = "User_Profile_Image"

//    Firebase database field names
    const val NAME: String = "name"
    const val USER_ROLE: String = "userRole"
    const val EMAIL: String = "email"
    const val USER_IMAGE: String = "image"
    const val MONTHLY_INCOME: String = "monthlyIncome"
    const val MOBILE_NUMBER: String = "mobileNumber"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}