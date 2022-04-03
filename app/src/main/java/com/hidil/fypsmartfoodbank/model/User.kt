package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val userRole: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val monthlyIncome: String = "",
    val mobileNumber: String = "",
    val state: String = "",
    val city: String = ""
) : Parcelable

