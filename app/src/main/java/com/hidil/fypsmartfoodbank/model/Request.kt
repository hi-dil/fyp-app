package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Request(
    var id: String = "",
    val completed: Boolean = false,
    val approved: Boolean = false,
    val foodBankImage: String = "",
    val foodBankID: String = "",
    val foodBankName: String = "",
    val requestDate: Long = 0,
    val lastUpdate: Long = 0,
    val userID: String = "",
    val items: ArrayList<ItemList> = ArrayList(),
    val address: String = "",
    val lat: String = "",
    val long: String = ""
) : Parcelable
