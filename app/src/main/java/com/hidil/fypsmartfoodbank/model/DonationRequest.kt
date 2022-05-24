package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DonationRequest(
    var id: String = "",
    val completed: Boolean = false,
    var approved: Boolean = false,
    var isCancel: Boolean = false,
    var isDenied: Boolean = false,
    var deniedMessage: String = "",
    val foodBankImage: String = "",
    val foodBankID: String = "",
    val foodBankName: String = "",
    val requestDate: Long = 0,
    var lastUpdate: Long = 0,
    val userID: String = "",
    val userImage: String = "",
    val userMobile: String = "",
    val userName: String = "",
    val items: ArrayList<ItemListDonation> = ArrayList(),
    val address: String = "",
    val lat: String = "",
    val long: String = "",
    val requestImages: ArrayList<String> = ArrayList()
) : Parcelable
