package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentStorageInfoBinding
import com.hidil.fypsmartfoodbank.model.Storage
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.AccessHistoryListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.StorageInfoViewModel

class StorageInfoFragment : Fragment() {

    private var _binding: FragmentStorageInfoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StorageInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageInfoBinding.inflate(inflater, container, false)
        DatabaseRepo().searchStorageDetails(this, args.storageID)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getStorageData(storageInfo: Storage) {
        binding.tvTitle.text = storageInfo.storageName
        GlideLoader(requireContext()).loadStoragePicture(storageInfo.itemImage, binding.ivStorageItem)
        binding.tvCurrentlyStoring.text = storageInfo.item
        binding.tvItemTypes.text = storageInfo.itemTypes
        binding.maxCapacity.text = "${storageInfo.maximumCapacity} items"

        binding.rvFoodBankAccessHistory.layoutManager = LinearLayoutManager(activity)
        binding.rvFoodBankAccessHistory.setHasFixedSize(true)
        val accessHistoryListAdapter = AccessHistoryListAdapter(requireActivity(), storageInfo.accessHistory, this)
        binding.rvFoodBankAccessHistory.adapter = accessHistoryListAdapter
    }


}