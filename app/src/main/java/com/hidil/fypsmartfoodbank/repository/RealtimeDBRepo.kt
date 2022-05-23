package com.hidil.fypsmartfoodbank.repository

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.hidil.fypsmartfoodbank.model.RealtimeDBPIN
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.QRScannerFragment
import com.hidil.fypsmartfoodbank.ui.fragments.donator.QRScannerDonatorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RealtimeDBRepo {
    private val mDatabase =
        FirebaseDatabase.getInstance("https://smart-foodbank-default-rtdb.asia-southeast1.firebasedatabase.app")

    fun unlockStorage(fragment: Fragment, storageID: String) {
        val ref = mDatabase.getReference(storageID)

        ref.child("/isUnlock").setValue(true).addOnSuccessListener {
            when (fragment) {
                is QRScannerFragment -> fragment.showSuccessDialog()
                is QRScannerDonatorFragment -> fragment.showSuccessDialog()
            }
        }.addOnFailureListener {
            when (fragment) {
                is QRScannerFragment -> fragment.showFailureDialog()
                is QRScannerDonatorFragment -> fragment.showFailureDialog()
            }
        }
    }

    suspend fun getListOfPin(storageID: String) {
        withContext(Dispatchers.IO) {
            val ref = mDatabase.getReference(storageID)

            val data = ref.child("/PIN").get().await().value
            Log.i("data", data.toString())
        }
    }

    suspend fun updatePin(
        storageID: String,
        pinData: RealtimeDBPIN,
        pinNumber: String,
        fragment: Fragment
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val ref = mDatabase.getReference(storageID)
            var success = false

            ref.child("/PIN").child(pinNumber).setValue(pinData).addOnSuccessListener {
                success = true
            }.addOnFailureListener {
                success = false
            }.await()

            return@withContext success
        }
    }
}