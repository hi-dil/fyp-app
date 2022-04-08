package com.hidil.fypsmartfoodbank.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemList(
   val itemName: String = "",
   val itemQuantity: String = "",
   val storageID: String = "",
   val storageName: String = "",
) : Parcelable
