package com.hidil.fypsmartfoodbank.ui.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentViewFavFoodBankBinding
import com.hidil.fypsmartfoodbank.ui.adapter.ViewFavFoodBankListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.viewModel.ViewFavFoodBankViewModel

class ViewFavFoodBankFragment : Fragment() {
    private var _binding: FragmentViewFavFoodBankBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ViewFavFoodBankFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewFavFoodBankBinding.inflate(inflater, container, false)

        val userDetails = args.userDetail
        val sp = activity!!.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val currentLat = sp.getString(Constants.CURRENT_LAT, "")
        val currentLong = sp.getString(Constants.CURRENT_LONG, "")

        binding.rvFavFoodBankList.layoutManager = LinearLayoutManager(activity)
        binding.rvFavFoodBankList.setHasFixedSize(true)
        val adapter = ViewFavFoodBankListAdapter(
            requireActivity(),
            userDetails.favouriteFoodBank,
            this,
            currentLat!!.toDouble(),
            currentLong!!.toDouble()
        )
        binding.rvFavFoodBankList.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}