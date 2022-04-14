package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccessHistory(
    var id: String = "",
    val userName: String = "",
    val userID: String = "",
    val amountTook: Int = 0,
    val lastVisited: String = "",
    val userImage: String = ""
): Parcelable
