package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentFoodBankInfoBinding
import com.hidil.fypsmartfoodbank.model.*
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.DonatorActivity
import com.hidil.fypsmartfoodbank.ui.adapter.AvailableFoodBankItemsListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class FoodBankInfoFragment : Fragment() {
    private var _binding: FragmentFoodBankInfoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FoodBankInfoFragmentArgs>()
    private var favouriteFoodbank: ArrayList<FavouriteFoodBank> = ArrayList()

    private var totalItems = 0

    private lateinit var mFoodBank: FoodBank
    private var isFavouriteFoodbank = false
    private var alreadyFavourite = false

    private var itemAmount: ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodBankInfoBinding.inflate(inflater, container, false)
        getFoodBankData()

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        Log.i("foodbankID", args.foodBankID)

        if (args.fromLocationFragment) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        view?.findNavController()?.navigate(
                            FoodBankInfoFragmentDirections.actionFoodBankInfoFragmentToLocationFragment()
                        )
                    }
                })
        }

        if (activity is DonatorActivity) {
            binding.btnConfirmRequest.text = "I want to donate the items"
            binding.tvAddMore.visibility = View.GONE
        }

        val favouriteFoodbankList = args.userData.favouriteFoodBank

        for (list in favouriteFoodbankList) {
            if (list.foodBankID == args.foodBankID) {
                isFavouriteFoodbank = true
                alreadyFavourite = true
            }
            Log.i(
                "test",
                "favourite ${args.foodBankID} ${list.foodBankID} ${list.foodBankID == args.foodBankID}"
            )
        }

        if (isFavouriteFoodbank) {
            binding.ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favourite
                )
            )
        }


        binding.ivFavourite.setOnClickListener {
            isFavouriteFoodbank = if (isFavouriteFoodbank) {
                binding.ivFavourite.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_favourite_empty
                    )
                )
                false
            } else {
                binding.ivFavourite.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_favourite
                    )
                )
                true
            }
        }


        return binding.root
    }

    override fun onPause() {
        super.onPause()
        val foodBankData = FavouriteFoodBank(
            args.foodBankID,
            mFoodBank.foodBankImage,
            mFoodBank.foodBankName,
            mFoodBank.address,
            mFoodBank.lat,
            mFoodBank.long
        )

        val updateFavFoodbank = args.userData.favouriteFoodBank

        if (alreadyFavourite && !isFavouriteFoodbank) {
            updateFavFoodbank.remove(foodBankData)
            val newUserData = args.userData
            newUserData.favouriteFoodBank = updateFavFoodbank
            DatabaseRepo().updateUserProfile(this, newUserData)

        } else if (!alreadyFavourite && isFavouriteFoodbank) {
            updateFavFoodbank.add(foodBankData)
            val newUserData = args.userData
            newUserData.favouriteFoodBank = updateFavFoodbank
            DatabaseRepo().updateUserProfile(this, newUserData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        totalItems = 0
        itemAmount = ArrayList()
        binding.btnConfirmRequest.setOnClickListener { view ->
            if (totalItems === 0) {
                Toast.makeText(
                    requireContext(),
                    "Please add items first before make a request",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val requestItemList: ArrayList<ItemList> = ArrayList()
                itemAmount.forEachIndexed { index, element ->
                    val storage = mFoodBank.storage[index]
                    if (element > 0) {
                        val itemList = ItemList(
                            false,
                            storage.item,
                            element,
                            storage.itemImage,
                            storage.id,
                            storage.storageName
                        )
                        requestItemList.add(itemList)
                    }
                }

                val currentRequest = Request(
                    "",
                    false,
                    approved = false,
                    cancel = false,
                    denied = false,
                    deniedMessage = "",
                    foodBankImage = mFoodBank.foodBankImage,
                    foodBankID = mFoodBank.id,
                    foodBankName = mFoodBank.foodBankName,
                    requestDate = 0,
                    lastUpdate = 0,
                    userID = AuthenticationRepo().getCurrentUserID(),
                    userImage = "",
                    userName = "",
                    userMobile = "",
                    items = requestItemList,
                    address = mFoodBank.address,
                    lat = mFoodBank.lat,
                    long = mFoodBank.long
                )

                view.findNavController().navigate(
                    FoodBankInfoFragmentDirections.actionFoodBankInfoFragmentToConfimRequestFragment(
                        currentRequest, mFoodBank
                    )
                )
            }

        }
    }


    private fun getFoodBankData() {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val foodbank = DatabaseRepo().searchFoodBank(this@FoodBankInfoFragment, args.foodBankID)
                Log.i("foodbankDetail", args.foodBankID)
                if (foodbank.size > 0) {
                    val foodBank = foodbank[0]
                    mFoodBank = foodBank

                    requireActivity().runOnUiThread {
                        GlideLoader(requireContext()).loadFoodBankPicture(foodBank.foodBankImage, binding.ivHeader)
                        binding.tvAddress.text = foodBank.address
                        binding.tvTitle.text = foodBank.foodBankName

                        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
                        val lat = sp?.getString(Constants.CURRENT_LAT, "")
                        val long = sp!!.getString(Constants.CURRENT_LONG, "")

                        val distance = distanceInKm(mFoodBank.lat.toDouble(), mFoodBank.long.toDouble(), lat!!.toDouble(), long!!.toDouble())
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        val rfDistance = df.format(distance)

                        binding.tvHowFar.text = "${rfDistance}km away"

                        val storage = mFoodBank.storage
                        val mostClaimed = storage.sortedWith(compareBy { it.amountClaimed })

                        binding.rvAvailableItems.layoutManager = LinearLayoutManager(activity)
                        binding.rvAvailableItems.setHasFixedSize(true)
                        val availableListAdapter =
                            AvailableFoodBankItemsListAdapter(requireActivity(), foodBank.storage, this@FoodBankInfoFragment)
                        binding.rvAvailableItems.adapter = availableListAdapter

                        for (i in foodBank.storage) {
                            itemAmount.add(0)
                        }
                    }
                }
            }
        }

    }

    fun changeItemAmount(position: Int, isAdd: Boolean) {
        if (isAdd) {
            itemAmount[position]++
        } else {
            itemAmount[position]--
        }
        totalItems = 0

        for (i in itemAmount) {
            totalItems += i
        }

        binding.tvTotalAmount.text = "${totalItems}/20"
        binding.tvAddMore.text = "You can add ${20 - totalItems} more items to your request"
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