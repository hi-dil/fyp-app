package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessHistory(
    val userName: String = "",
    val userID: String = "",
    val amountTook: Int = 0,
    val lastVisited: Long = 0,
    val userImage: String = ""
): Parcelable
