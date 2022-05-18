package com.hidil.fypsmartfoodbank.repository

import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.QRScannerFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.QRScannerFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.donator.QRScannerDonatorFragment

class RealtimeDBRepo {
    val mDatabase = FirebaseDatabase.getInstance("https://smart-foodbank-default-rtdb.asia-southeast1.firebasedatabase.app")

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
}