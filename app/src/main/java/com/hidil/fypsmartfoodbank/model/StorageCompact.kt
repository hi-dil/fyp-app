package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StorageCompact(
    var id: String = "",
    val amountClaimed: Int = 0,
    val storageName: String = "",
    val itemImage: String = "",
    val item: String = "",
    val itemTypes: String = "",
    var itemQuantity: Int = 0,
    val maximumCapacity: Int = 0
) : Parcelable
