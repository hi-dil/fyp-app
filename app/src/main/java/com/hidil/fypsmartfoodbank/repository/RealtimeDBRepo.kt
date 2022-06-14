package com.hidil.fypsmartfoodbank.repository

import android.util.Log
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

    suspend fun unlockStorageAsync(fragment: Fragment, storageID: String): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false
            val ref = mDatabase.getReference(storageID)

            ref.child("/isUnlock").setValue(true).addOnSuccessListener { success = true }
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

    suspend fun getListOfPin(storageID: String): HashMap<String, Any> {
        return withContext(Dispatchers.IO) {
            val ref = mDatabase.getReference(storageID)
            val data= ref.child("/PIN").get().await().value
            var pinlist = HashMap<String, Any>()


            if (data != null) {
                pinlist = data as HashMap<String, Any>
            }

            return@withContext pinlist
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

    suspend fun setCurrentPinAsync(pinNumber: Int, storageID: String, fragment: Fragment): Boolean {
        return withContext(Dispatchers.IO) {
            val ref = mDatabase.getReference(storageID)
            var success = false

            ref.child("/currentPin").setValue(pinNumber).addOnSuccessListener { success = true }
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

    fun unlockStorage(fragment: Fragment, storageID: String, currentPin: Int) {
        val ref = mDatabase.getReference(storageID)

        ref.child("/isUnlock").setValue(true).addOnSuccessListener {
            when (fragment) {
                is QRScannerFragment -> setCurrentPin(fragment, storageID, currentPin)
                is QRScannerDonatorFragment -> fragment.showSuccessDialog()
            }
        }.addOnFailureListener {
            when (fragment) {
                is QRScannerFragment -> fragment.showFailureDialog()
                is QRScannerDonatorFragment -> fragment.showFailureDialog()
            }
        }
    }

    fun setCurrentPin(fragment: Fragment, storageID: String, pinNumber: Int) {
        val ref = mDatabase.getReference(storageID)

        ref.child("/currentPin").setValue(pinNumber).addOnSuccessListener {
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