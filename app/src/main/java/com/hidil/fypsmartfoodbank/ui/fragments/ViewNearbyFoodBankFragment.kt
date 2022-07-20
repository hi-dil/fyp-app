package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.ViewNearbyFoodBankViewModel

class ViewNearbyFoodBankFragment : Fragment() {

    companion object {
        fun newInstance() = ViewNearbyFoodBankFragment()
    }

    private lateinit var viewModel: ViewNearbyFoodBankViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_nearby_food_bank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewNearbyFoodBankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}