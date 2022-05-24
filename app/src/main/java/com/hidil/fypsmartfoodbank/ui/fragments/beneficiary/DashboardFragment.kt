package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardBinding
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.ui.adapter.FavouriteFoodBankListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.NearbyFoodBankListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.beneficiary.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // get info from intent
        val activeRequest =
            requireActivity().intent.getParcelableArrayListExtra<Request>("activeRequest")
        val userDetails = requireActivity().intent.getParcelableExtra<User>("userDetails")
        val locationList =
            requireActivity().intent.getParcelableArrayListExtra<Location>("locationList")

        // get user info from shared pref
        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp?.getString(Constants.LOGGED_IN_USER, "")
        val userImage = sp!!.getString(Constants.USER_PROFILE_IMAGE, "")
        val city = sp.getString(Constants.USER_CITY, "")
        val state = sp.getString(Constants.USER_STATE, "")
        currentLat = sp.getString(Constants.CURRENT_LAT, "")!!.toDouble()
        currentLong = sp.getString(Constants.CURRENT_LONG, "")!!.toDouble()

        // attach info to the views
        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"
        if (userImage != null) {
            GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)
        }


        if (locationList != null) {
            attachNearbyFoodBank(locationList)
        }

        if (userDetails != null) {
            val favouriteFoodBankList = userDetails.favouriteFoodBank
            attachFavouriteFoodBank(favouriteFoodBankList)
        }
        if (activeRequest != null) {
            attachActiveRequest(activeRequest)
        }

        //add function when swipe
        binding.swipeToRefresh.setOnRefreshListener {
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val location = DatabaseRepo().getLocationAsync()
                    val sortList = sortLocation(location)
                    val updatedLocationList = ArrayList(sortList)
                    val updatedActiveRequest = DatabaseRepo().getActiveRequestAsync()
                    val updatedUserDetails = DatabaseRepo().getUserDetailAsync()

                    requireActivity().runOnUiThread {
                        attachActiveRequest(updatedActiveRequest)
                        attachFavouriteFoodBank(updatedUserDetails.favouriteFoodBank)
                        attachNearbyFoodBank(updatedLocationList)

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
        if (requireActivity() is BeneficiaryMainActivity) {
            (activity as BeneficiaryMainActivity?)!!.showBottomNavigationView()
        }
    }

    private fun attachActiveRequest(activeRequestList: ArrayList<Request>) {

        if (activeRequestList.size > 0) {
            binding.rvActiveRequest.visibility = View.VISIBLE
            binding.tvNoActiveRequest.visibility = View.GONE

            binding.rvActiveRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvActiveRequest.setHasFixedSize(true)
            val activeRequestAdapter =
                ActiveRequestListAdapter(requireActivity(), activeRequestList, this)
            binding.rvActiveRequest.adapter = activeRequestAdapter
        } else {
            binding.rvActiveRequest.visibility = View.GONE
            binding.tvNoActiveRequest.visibility = View.VISIBLE
        }

    }

    private fun attachFavouriteFoodBank(favouriteFoodBankList: ArrayList<FavouriteFoodBank>) {
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

    private fun sortLocation(list: ArrayList<Location>): List<Location> {
        for (i in list) {
            val distance =
                distanceInKm(currentLat, currentLong, i.lat.toDouble(), i.long.toDouble())
            i.distance = distance
        }

        return list.sortedWith(compareBy { it.distance })
    }

    private fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}