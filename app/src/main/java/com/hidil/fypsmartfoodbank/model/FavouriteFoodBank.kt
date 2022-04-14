package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavouriteFoodBank (
    val foodBankID: String = "",
    val foodBankImage: String = "",
    val foodBankName: String = "",
    val address: String = "",
    val lat: String = "",
    val long: String = ""
) : Parcelable