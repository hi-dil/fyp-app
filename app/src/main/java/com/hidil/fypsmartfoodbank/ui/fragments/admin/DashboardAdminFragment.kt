package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardAdminBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
import com.hidil.fypsmartfoodbank.ui.activity.AdminMainActivity
import com.hidil.fypsmartfoodbank.ui.adapter.admin.DonationRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.admin.RequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardAdminFragment : Fragment() {

    private var _binding: FragmentDashboardAdminBinding? = null
    private val binding get() = _binding!!

    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0

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

        val sp = requireActivity().getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp.getString(Constants.LOGGED_IN_USER, "")
        val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")
        val city = sp.getString(Constants.USER_CITY, "")
        val state = sp.getString(Constants.USER_STATE, "")

        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"

        if (userImage != null) {
            GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)
        }

        if (claimRequest != null) {
            attachClaimRequest(claimRequest)
        }
        if (donationRequest != null) {
            attachDonationRequest(donationRequest)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is AdminMainActivity) (requireActivity() as AdminMainActivity).showBottomNavigationView()
    }

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