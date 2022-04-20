package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FoodBankInfoFragmentBinding
import com.hidil.fypsmartfoodbank.databinding.FragmentFoodBankLocationBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.AvailableFoodBankItemsListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.FoodBankInfoViewModel

class FoodBankInfoFragment : Fragment() {
    private var _binding: FoodBankInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FoodBankInfoFragmentArgs>()

    private lateinit var viewModel: FoodBankInfoViewModel
    private lateinit var mFoodBank: FoodBank

    private var itemAmount: ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val foodBankInfoViewModel = ViewModelProvider(this).get(FoodBankInfoViewModel::class.java)
        _binding = FoodBankInfoFragmentBinding.inflate(inflater, container, false)
        DatabaseRepo().searchFoodBankDetails(this, args.foodBankID)

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnConfirmRequest.setOnClickListener {
            confirmRequest()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmRequest() {

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
            false,
            mFoodBank.foodBankImage,
            mFoodBank.id,
            mFoodBank.foodBankName,
            "",
            "",
            AuthenticationRepo().getCurrentUserID(),
            requestItemList,
            mFoodBank.address,
            mFoodBank.lat,
            mFoodBank.long
        )

    }

    fun getFoodBankData(foodBank: FoodBank) {
        mFoodBank = foodBank
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

        var totalItems = 0
        for (i in itemAmount) {
            totalItems += i
        }

        binding.tvTotalAmount.text = "${totalItems}/20"
        binding.tvAddMore.text = "You can add ${20 - totalItems} more items to your request"
    }


}