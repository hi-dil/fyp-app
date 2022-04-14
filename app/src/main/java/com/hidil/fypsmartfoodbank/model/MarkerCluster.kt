package com.hidil.fypsmartfoodbank.model

import androidx.fragment.app.Fragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterItem

class MarkerCluster(
    private val position: LatLng,
    private val title: String,
    private val item: Location
) : ClusterItem {
    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        val snippets = Gson().toJson(item)
        return snippets
    }

}