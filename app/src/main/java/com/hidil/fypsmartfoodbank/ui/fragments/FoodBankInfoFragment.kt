package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FoodBankInfoFragmentBinding
import com.hidil.fypsmartfoodbank.databinding.FragmentFoodBankLocationBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
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
        binding.tvTitle.text = args.currentFoodBank.foodBankName
        binding.tvAddress.text = args.currentFoodBank.address
        GlideLoader(requireContext()).loadFoodBankPicture(
            args.currentFoodBank.foodBankImage,
            binding.ivHeader
        )

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getFoodBankData(foodBank: ArrayList<FoodBank>) {
        if (foodBank.size > 0) {

        }
    }

}