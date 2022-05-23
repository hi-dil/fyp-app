package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemListDonation(
    val completed: Boolean = false,
    val itemName: String = "",
    val itemQuantity: Int = 0,
    val itemImage: String = "",
    val storageID: String = "",
    val storageName: String = "",
    var storagePIN: String = "",
    val expiryDate: Long = 0,
    val itemBrand: String = "",
): Parcelable
