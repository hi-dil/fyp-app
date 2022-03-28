package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hidil.fypsmartfoodbank.ui.activity.SignUp
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.utils.Constants

class StorageRepo {
    val mStorage = FirebaseStorage.getInstance()

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?) {
        val sRef: StorageReference = mStorage.reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )

        sRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Image URL: ", uri.toString())
//                        when (activity) {
//                            is SignUp -> {
//                                activity.imageUploadSuccess(uri.toString())
//                            }
//                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is SignUp -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }

    }
}