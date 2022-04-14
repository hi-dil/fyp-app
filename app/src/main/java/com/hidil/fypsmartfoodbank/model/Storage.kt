package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Storage(
    var id: String = "",
    val storageName: String = "",
    val itemImage: String = "",
    val item: String = "",
    val itemTypes: String = "",
    val itemQuantity: String = "",
    val maximumCapacity: Int = 0,
    val expiryDate: String = "",
    val itemType: String = ""
): Parcelable
