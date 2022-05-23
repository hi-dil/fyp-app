package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RealtimeDBPIN(
    val dayCreated: Long = 0,
    val requestID: String = "",
    val requestType: String = ""
) : Parcelable