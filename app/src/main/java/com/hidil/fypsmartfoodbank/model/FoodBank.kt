package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodBank(
    var id: String = "",
    val address: String = "",
    val foodBankImage: String = "",
    val foodBankName: String = "",
    val storage: ArrayList<StorageCompact> = ArrayList(),
    val lat: String = "",
    val long: String = ""
): Parcelable
