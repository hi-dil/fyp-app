package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Request(
    var id: String = "",
    var completed: Boolean = false,
    var approved: Boolean = false,
    var cancel: Boolean = false,
    var denied: Boolean = false,
    var deniedMessage: String = "",
    val foodBankImage: String = "",
    val foodBankID: String = "",
    val foodBankName: String = "",
    val requestDate: Long = 0,
    var lastUpdate: Long = 0,
    val userID: String = "",
    val userImage: String = "",
    val userName: String = "",
    val userMobile: String = "",
    val items: ArrayList<ItemList> = ArrayList(),
    val address: String = "",
    val lat: String = "",
    val long: String = ""
) : Parcelable
