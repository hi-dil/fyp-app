package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.Inet4Address

@Parcelize
data class Location(
    val address: String = "",
    var foodBankID: String = "",
    val foodBankImage: String = "",
    val foodBankName: String = "",
    val lat: String = "",
    val long: String = ""
): Parcelable
