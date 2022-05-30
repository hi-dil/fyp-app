package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

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
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.DonatorActivity
import com.hidil.fypsmartfoodbank.ui.adapter.AvailableFoodBankItemsListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class FoodBankInfoFragment : Fragment() {
    private var _binding: FragmentFoodBankInfoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FoodBankInfoFragmentArgs>()
    private var favouriteFoodbank: ArrayList<FavouriteFoodBank> = ArrayList()

    private var totalItems = 0

    private lateinit var mFoodBank: FoodBank
    var isFavouriteFoodbank = false
    var alreadyFavourite = false

    private var itemAmount: ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodBankInfoBinding.inflate(inflater, container, false)
        DatabaseRepo().searchFoodBankDetails(this, args.foodBankID)

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

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


    fun getFoodBankData(foodBank: FoodBank) {
        mFoodBank = foodBank
        Log.i("test", mFoodBank.id)
        GlideLoader(requireContext()).loadFoodBankPicture(foodBank.foodBankImage, binding.ivHeader)
        binding.tvAddress.text = foodBank.address
        binding.tvTitle.text = foodBank.foodBankName

        binding.rvAvailableItems.layoutManager = LinearLayoutManager(activity)
        binding.rvAvailableItems.setHasFixedSize(true)
        val availableListAdapter =
            AvailableFoodBankItemsListAdapter(requireActivity(), foodBank.storage, this)
        binding.rvAvailableItems.adapter = availableListAdapter

        for (i in foodBank.storage) {
            itemAmount.add(0)
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

//        itemAmount.forEachIndexed { index, element ->
//            val storage = mFoodBank.storage[index]
//
//            if (element > storage.itemQuantity) {
//                Toast.makeText(requireContext(), "cannot add more than current stock", Toast.LENGTH_SHORT).show()
//            }
//        }



        binding.tvTotalAmount.text = "${totalItems}/20"
        binding.tvAddMore.text = "You can add ${20 - totalItems} more items to your request"
    }


}