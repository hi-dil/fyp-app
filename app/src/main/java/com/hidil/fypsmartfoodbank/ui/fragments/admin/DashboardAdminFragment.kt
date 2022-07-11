package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardAdminBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.AdminMainActivity
import com.hidil.fypsmartfoodbank.ui.adapter.admin.DonationRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.admin.RequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardAdminFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentDashboardAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardAdminBinding.inflate(inflater, container, false)

        val claimRequest =
            requireActivity().intent.getParcelableArrayListExtra<Request>("claimRequest")
        val donationRequest =
            requireActivity().intent.getParcelableArrayListExtra<DonationRequest>("donationRequest")

        // update the shared preference to store the user's data locally
        val sp = requireActivity().getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp.getString(Constants.LOGGED_IN_USER, "")
        val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")
        val city = sp.getString(Constants.USER_CITY, "")
        val state = sp.getString(Constants.USER_STATE, "")

        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"


        // check if the intent's content is not null
        if (userImage != null) {
            GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)
        }
        if (claimRequest != null) {
            attachClaimRequest(claimRequest)
        }
        if (donationRequest != null) {
            attachDonationRequest(donationRequest)
        }

        // get user's last known location
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (hasLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val sp =
                        activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
                    val spEditor = sp!!.edit()
                    spEditor.putString(Constants.CURRENT_LAT, location.latitude.toString())
                    spEditor.putString(Constants.CURRENT_LONG, location.longitude.toString())
                    spEditor.apply()
                }
            }
        } else {
            requestLocationPermission()
        }

        binding.swipeToRefresh.setOnRefreshListener {
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val oldestDonationRequest = DatabaseRepo().getOldDonationRequest(3)
                    val oldestClaimRequest = DatabaseRepo().getOldestClaimRequest(3)

                    requireActivity().runOnUiThread {
                        attachClaimRequest(oldestClaimRequest)
                        attachDonationRequest(oldestDonationRequest)
                        binding.swipeToRefresh.isRefreshing = false
                    }
                }
            }
        }


        return binding.root
    }

    // request for location permission
    private fun hasLocationPermission() = (
            EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            )
            )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this, "This application cannot work without location permission",
            Constants.LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // store user's current location to the shared prefs
                val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
                val spEditor = sp!!.edit()
                spEditor.putString(Constants.CURRENT_LAT, location.latitude.toString())
                spEditor.putString(Constants.CURRENT_LONG, location.longitude.toString())
                spEditor.apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // show bottom nav if user has come back to main nav page
    override fun onResume() {
        super.onResume()
        if (requireActivity() is AdminMainActivity) (requireActivity() as AdminMainActivity).showBottomNavigationView()
    }

    // load the claim request data to the admin
    private fun attachClaimRequest(request: ArrayList<Request>) {
        if (request.size > 0) {
            binding.rvOldClaimRequest.visibility = View.VISIBLE
            binding.tvOldClaimRequest.visibility = View.GONE

            binding.rvOldClaimRequest.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvOldClaimRequest.setHasFixedSize(true)
            val oldClaimRequestAdapter =
                RequestListAdapter(requireContext(), request, this)
            binding.rvOldClaimRequest.adapter = oldClaimRequestAdapter
        } else {
            binding.rvOldClaimRequest.visibility = View.GONE
            binding.tvOldClaimRequest.visibility = View.VISIBLE
        }
    }

    // load the donation request data to the admin
    private fun attachDonationRequest(request: ArrayList<DonationRequest>) {
        if (request.size > 0) {
            binding.tvOldDonationRequest.visibility = View.GONE
            binding.rvOldDonationRequest.visibility = View.VISIBLE

            binding.rvOldDonationRequest.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvOldDonationRequest.setHasFixedSize(true)
            val oldDonationRequestAdapter =
                DonationRequestListAdapter(requireContext(), request, this)
            binding.rvOldDonationRequest.adapter = oldDonationRequestAdapter
        } else {
            binding.rvOldDonationRequest.visibility = View.GONE
            binding.tvOldDonationRequest.visibility = View.VISIBLE
        }
    }

}