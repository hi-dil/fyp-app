package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.StorageInfoViewModel

class StorageInfoFragment : Fragment() {

    companion object {
        fun newInstance() = StorageInfoFragment()
    }

    private lateinit var viewModel: StorageInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.storage_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StorageInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}