package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.LocationListViewViewModel

class LocationListViewFragment : Fragment() {

    companion object {
        fun newInstance() = LocationListViewFragment()
    }

    private lateinit var viewModel: LocationListViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_list_view_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationListViewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}