package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardDonatorBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.donator.DashboardViewModel

class DashboardDonatorFragment : Fragment() {

    private var _binding: FragmentDashboardDonatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardDonatorBinding.inflate(inflater, container, false)

        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp?.getString(Constants.LOGGED_IN_USER, "")!!
        val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")!!
        val city = sp.getString(Constants.USER_CITY, "")!!
        val state = sp.getString(Constants.USER_STATE, "")!!

        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"
        GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)

        DatabaseRepo().getDonationActiveRequest(this, AuthenticationRepo().getCurrentUserID())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getActiveRequest(activeRequestList: ArrayList<DonationRequest>) {
        if (activeRequestList.size > 0) {
            binding.rvActiveRequest.visibility = View.VISIBLE
            binding.tvNoActiveRequest.visibility = View.GONE

            binding.rvActiveRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvActiveRequest.setHasFixedSize(true)
            val activeRequestAdapter = ActiveRequestListAdapter(requireActivity(), activeRequestList, this)
            binding.rvActiveRequest.adapter = activeRequestAdapter
        } else {
            binding.rvActiveRequest.visibility = View.GONE
            binding.tvNoActiveRequest.visibility = View.VISIBLE
        }
    }


}