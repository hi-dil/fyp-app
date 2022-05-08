package com.hidil.fypsmartfoodbank.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.ui.fragments.QRScannerFragment

class RealtimeDBRepo {
    val mDatabase = FirebaseDatabase.getInstance("https://smart-foodbank-default-rtdb.asia-southeast1.firebasedatabase.app")

    fun unlockStorage(fragment: QRScannerFragment, storageID: String) {
        val ref = mDatabase.getReference(storageID)

        ref.child("/isUnlock").setValue(true).addOnSuccessListener {
            Log.i("firebase", "Successfully unlock the storage")
            fragment.showSuccessDialog()
        }.addOnFailureListener {
            Log.i("firebase", "Failed to unlock the storage")
            fragment.showFailureDialog()
        }
    }
}