package com.hidil.fypsmartfoodbank.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class CustomInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    private val contentView = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    override fun getInfoContents(marker: Marker): View? {
        renderViews(marker, contentView)
        return contentView
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderViews(marker, contentView)
        return contentView
    }

    private fun renderViews(marker: Marker?, contentView: View) {
        val title = marker?.title
        val type = object : TypeToken<Location>() {}.type
        val item: Location = Gson().fromJson(marker?.snippet, type)

        val titleTextView = contentView.findViewById<TextView>(R.id.tv_ciwTitle)
        val addressTextView = contentView.findViewById<TextView>(R.id.tv_ciwDescription)
        val foodBankImage = contentView.findViewById<ImageView>(R.id.iv_placeImage)

        titleTextView.text = title
        addressTextView.text = item.address
        GlideLoader(context).loadFoodBankPicture(item.foodBankImage, foodBankImage)
    }
}