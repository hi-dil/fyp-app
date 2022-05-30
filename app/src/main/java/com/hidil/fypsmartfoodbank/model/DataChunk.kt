package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataChunk (
    val collectionName: String = "",
    val location: ArrayList<Location> = ArrayList()
): Parcelable