package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Storage(
    var id: String = "",
    val storageName: String = "",
    val itemImage: String = "",
    val item: String = "",
    val itemTypes: String = "",
    val itemQuantity: Int = 0,
    val maximumCapacity: Int = 0,
    val expiryDate: Long = 0,
    val accessHistory: ArrayList<AccessHistory> = ArrayList()
): Parcelable
