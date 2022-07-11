package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentLocationAdminBinding
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.model.MarkerCluster
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.AdminMainActivity
import com.hidil.fypsmartfoodbank.ui.adapter.CustomInfoWindowAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAdminFragment : Fragment(), OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<MarkerCluster>,
    ClusterManager.OnClusterClickListener<MarkerCluster> {


    private var _binding: FragmentLocationAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var mLocation = ArrayList<Location>()
    private lateinit var clusterManager: ClusterManager<MarkerCluster>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationAdminBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.APP_PREF,
            Context.MODE_PRIVATE
        )
        val location = sharedPreferences.getString(Constants.LOCATION_ARRAYLIST, null)
        val type = object : TypeToken<ArrayList<Location>>() {}.type
        mLocation = Gson().fromJson(location, type)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        when (activity) {
            is AdminMainActivity -> (activity as AdminMainActivity).showBottomNavigationView()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val malaysia = LatLng(1.435209, 107.960093)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 5f))

        map.uiSettings.apply {
            isMyLocationButtonEnabled = true
        }
        clusterManager = ClusterManager(requireActivity(), map)
        map.setOnCameraIdleListener(clusterManager)
        addMarkers()
        clusterManager.markerCollection.setInfoWindowAdapter(
            CustomInfoWindowAdapter(
                requireActivity()
            )
        )
        clusterManager.markerCollection.setOnInfoWindowClickListener { marker ->
            val type = object : TypeToken<Location>() {}.type
            val item: Location = Gson().fromJson(marker.snippet, type)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Default) {
                    requireActivity().runOnUiThread {

                        val action =
                            LocationAdminFragmentDirections.actionLocationAdminFragmentToFoodBankInfoAdminFragment(
                                item.foodBankID
                            )
                        findNavController().navigate(action)
                        when (activity) {
                            is AdminMainActivity -> (activity as AdminMainActivity).hideBottomNavigationView()
                        }
                    }

                }
            }

        }

        clusterManager.setOnClusterClickListener(this)

        checkLocationPermission()
    }

    override fun onClusterItemClick(item: MarkerCluster?): Boolean {
        if (item != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(item.position, 15f), 2000, null)
        }
        return true
    }

    override fun onClusterClick(cluster: Cluster<MarkerCluster>?): Boolean {
        if (cluster != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.position, 12f), 2000, null)
            Log.i("test", cluster.position.toString())
        }
        return true
    }


    private fun addMarkers() {
        for (i in mLocation) {
            val markerItem = MarkerCluster(
                LatLng(i.lat.toDouble(), i.long.toDouble()),
                i.foodBankName,
                i
            )
            clusterManager.addItem(markerItem)
        }
    }


    @SuppressLint("MissingPermission")
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != Constants.LOCATION_REQUEST_CODE) {
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity(), "Granted!", Toast.LENGTH_SHORT).show()
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(
                requireActivity(),
                "Please enable location permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}