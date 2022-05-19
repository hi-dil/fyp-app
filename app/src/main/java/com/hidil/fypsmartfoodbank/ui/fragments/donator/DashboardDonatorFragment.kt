package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardDonatorBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.ui.activity.DonatorActivity
import com.hidil.fypsmartfoodbank.ui.adapter.FavouriteFoodBankListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.NearbyFoodBankListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.donator.DashboardViewModel

class DashboardDonatorFragment : Fragment() {

    private var _binding: FragmentDashboardDonatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardDonatorBinding.inflate(inflater, container, false)

        val activeRequest =
            requireActivity().intent.getParcelableExtra<DonationRequest>("activeRequest")
        val userDetails = requireActivity().intent.getParcelableExtra<User>("userDetails")
        val locationList =
            requireActivity().intent.getParcelableArrayListExtra<Location>("locationList")

        val activeRequestList: ArrayList<DonationRequest> = ArrayList()

        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = userDetails?.name
        val userImage = userDetails!!.image
        val city = userDetails.city
        val state = userDetails.state
        currentLat = sp?.getString(Constants.CURRENT_LAT, "")!!.toDouble()
        currentLong = sp.getString(Constants.CURRENT_LONG, "")!!.toDouble()

        if (activeRequest != null) {
            activeRequestList.add(activeRequest)
            attachActiveRequest(activeRequestList)
        }

        val favouriteFoodBankList = userDetails.favouriteFoodBank
        attachFavFoodBank(favouriteFoodBankList)
        if (locationList != null) {
            attachNearbyFoodBank(locationList)
        }


        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"
        GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is DonatorActivity) {
            (activity as DonatorActivity?)!!.showBottomNavigationView()
        }
    }

    private fun attachActiveRequest(activeRequestList: ArrayList<DonationRequest>) {
        if (activeRequestList.size > 0 && activeRequestList[0] != DonationRequest()) {

            binding.rvActiveRequest.visibility = View.VISIBLE
            binding.tvNoActiveRequest.visibility = View.GONE

            binding.rvActiveRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvActiveRequest.setHasFixedSize(true)
            val activeRequestAdapter =
                ActiveRequestListAdapter(requireActivity(), activeRequestList, this)
            binding.rvActiveRequest.adapter = activeRequestAdapter
        }
    }

    private fun attachFavFoodBank(favouriteFoodBankList: ArrayList<FavouriteFoodBank>) {
        if (favouriteFoodBankList.size > 0) {
            binding.rvFavouriteFoodBank.visibility = View.VISIBLE
            binding.tvNoFavouriteFoodBank.visibility = View.GONE
            binding.tvViewAllFavouriteFoodBank.visibility = View.VISIBLE

            binding.rvFavouriteFoodBank.layoutManager = LinearLayoutManager(activity)
            binding.rvFavouriteFoodBank.setHasFixedSize(true)

            var maxSize = 0
            var compactFavouriteFoodBankList: ArrayList<FavouriteFoodBank> = ArrayList()

            if (favouriteFoodBankList.size > 3) {
                while (maxSize < 3) {
                    compactFavouriteFoodBankList.add(favouriteFoodBankList[maxSize])
                    maxSize++
                }
            } else {
                compactFavouriteFoodBankList = favouriteFoodBankList
            }


            val favouriteFoodBankAdapter = FavouriteFoodBankListAdapter(
                requireActivity(),
                compactFavouriteFoodBankList,
                this,
                currentLat,
                currentLong
            )
            binding.rvFavouriteFoodBank.adapter = favouriteFoodBankAdapter
        } else {
            binding.rvFavouriteFoodBank.visibility = View.GONE
            binding.tvNoFavouriteFoodBank.visibility = View.VISIBLE
            binding.tvViewAllFavouriteFoodBank.visibility = View.GONE
        }
    }

    private fun attachNearbyFoodBank(locationList: ArrayList<Location>) {
        binding.rvNearbyFoodBank.layoutManager = LinearLayoutManager(activity)
        binding.rvNearbyFoodBank.setHasFixedSize(true)

        var maxSize = 0
        var compactLocationList: ArrayList<Location> = ArrayList()

        if (locationList.size > 3) {
            while (maxSize < 3) {
                compactLocationList.add(locationList[maxSize])
                maxSize++
            }
        } else {
            compactLocationList = locationList
        }

        val nearbyFoodBankAdapter =
            NearbyFoodBankListAdapter(requireContext(), compactLocationList, this)
        binding.rvNearbyFoodBank.adapter = nearbyFoodBankAdapter
    }

}