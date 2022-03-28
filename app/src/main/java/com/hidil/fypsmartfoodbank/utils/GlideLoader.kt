package com.hidil.fypsmartfoodbank.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hidil.fypsmartfoodbank.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .placeholder(R.drawable.def_profile)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}