package com.hidil.fypsmartfoodbank.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hidil.fypsmartfoodbank.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.def_profile)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadFoodBankPicture(foodBankImage: String, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(foodBankImage)
                .centerCrop()
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}