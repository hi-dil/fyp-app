package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FoodBankInfoFragmentBinding
import com.hidil.fypsmartfoodbank.databinding.FragmentFoodBankLocationBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.AvailableFoodBankItemsListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.FoodBankInfoViewModel

class FoodBankInfoFragment : Fragment() {
    private var _binding: FoodBankInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FoodBankInfoFragmentArgs>()

    private lateinit var viewModel: FoodBankInfoViewModel

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getFoodBankData(foodBank: FoodBank) {
        GlideLoader(requireContext()).loadFoodBankPicture(foodBank.foodBankImage, binding.ivHeader)
        binding.tvAddress.text = foodBank.address
        binding.tvTitle.text = foodBank.foodBankName

        binding.rvAvailableItems.layoutManager = LinearLayoutManager(activity)
        binding.rvAvailableItems.setHasFixedSize(true)
        val availableListAdapter = AvailableFoodBankItemsListAdapter(requireActivity(), foodBank.storage, this)
        binding.rvAvailableItems.adapter = availableListAdapter
    }


}