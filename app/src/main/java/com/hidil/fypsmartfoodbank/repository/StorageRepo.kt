package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hidil.fypsmartfoodbank.ui.activity.EditProfileActivity
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.fragments.donator.ConfirmDonationRequestFragment
import com.hidil.fypsmartfoodbank.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StorageRepo {
    val mStorage = FirebaseStorage.getInstance()

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, fileName: String) {
        val sRef: StorageReference = mStorage.reference.child(
            fileName + System.currentTimeMillis() + "." + Constants.getFileExtension(
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
                        when (activity) {
                            is EditProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }

    }


    suspend fun uploadImageToCloudStorageFragment(fragment: Fragment, imageFileUri: Uri?, fileName: String): String {
        val sRef: StorageReference = mStorage.reference.child(
            fileName + System.currentTimeMillis() + "." + Constants.getFileExtension(
                fragment.requireActivity(),
                imageFileUri
            )
        )

        return withContext(Dispatchers.IO) {
            sRef.putFile(imageFileUri!!)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()
//                .addOnSuccessListener { taskSnapshot ->
//                    taskSnapshot.metadata!!.reference!!.downloadUrl
//                        .addOnSuccessListener { uri ->
//                            when (fragment) {
//                                is ConfirmDonationRequestFragment -> fragment.imageUploadSuccess(uri.toString())
//                            }
//                        }
//                }
//                .addOnFailureListener { exception ->
//                    Log.e(fragment.javaClass.simpleName, exception.message, exception)
//                }
//                .await()

        }


    }
}