package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentFoodBankInfoAdminBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.admin.AvailableFoodBankItemsListAdminAdapter
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.FoodBankInfoFragmentDirections
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class FoodBankInfoAdminFragment : Fragment() {
    private var _binding: FragmentFoodBankInfoAdminBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FoodBankInfoAdminFragmentArgs>()
    private lateinit var mFoodBank: FoodBank

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBankInfoAdminBinding.inflate(inflater, container, false)
        getFoodBankData()

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(
                        FoodBankInfoFragmentDirections.actionFoodBankInfoFragmentToLocationFragment()
                    )
                }
            })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // fetch data from firebase and display the data
    private fun getFoodBankData() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                val foodbank =
                    DatabaseRepo().searchFoodBank(this@FoodBankInfoAdminFragment, args.foodBankID)
                if (foodbank.size > 0) {
                    val foodBank = foodbank[0]
                    mFoodBank = foodBank

                    requireActivity().runOnUiThread {

                        // get user's location from shared prefs
                        val sp =
                            activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
                        val lat = sp?.getString(Constants.CURRENT_LAT, "")
                        val long = sp!!.getString(Constants.CURRENT_LONG, "")

                        val distance = distanceInKm(
                            mFoodBank.lat.toDouble(),
                            mFoodBank.long.toDouble(),
                            lat!!.toDouble(),
                            long!!.toDouble()
                        )

                        // convert the distance to two decimal points
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        val rfDistance = df.format(distance)

                        // display the food bank details
                        GlideLoader(requireContext()).loadFoodBankPicture(
                            foodBank.foodBankImage,
                            binding.ivHeader
                        )
                        binding.tvAddress.text = foodBank.address
                        binding.tvTitle.text = foodBank.foodBankName
                        binding.tvHowFar.text = "${rfDistance}km away"

                        // display available items from the foodbank
                        binding.rvAvailableItems.layoutManager = LinearLayoutManager(activity)
                        binding.rvAvailableItems.setHasFixedSize(true)
                        val availableListAdapter =
                            AvailableFoodBankItemsListAdminAdapter(
                                requireActivity(),
                                foodBank.storage,
                                this@FoodBankInfoAdminFragment
                            )
                        binding.rvAvailableItems.adapter = availableListAdapter
                    }
                }
            }
        }

    }

    // calculate the distance between two locations and return in km
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