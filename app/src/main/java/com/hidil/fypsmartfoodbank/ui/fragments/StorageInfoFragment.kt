package com.hidil.fypsmartfoodbank.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentStorageInfoBinding
import com.hidil.fypsmartfoodbank.model.AccessHistory
import com.hidil.fypsmartfoodbank.model.Storage
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
import com.hidil.fypsmartfoodbank.ui.adapter.AccessHistoryListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val distance = RealtimeDBRepo().getDistance(args.storageID)

                requireActivity().runOnUiThread {
                    binding.tvDistanceValue.text = "$distance cm"
                }
            }
        }

        binding.fabBack.setOnClickListener { requireActivity().onBackPressed() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getStorageData(storageInfo: Storage) {
        val beneficiaryAccessList = ArrayList<AccessHistory>()

        for (list in storageInfo.accessHistory) {
            if (list.requestType == "claim") {
                beneficiaryAccessList.add(list)
            }
        }
        binding.tvTitle.text = storageInfo.storageName
        GlideLoader(requireContext()).loadStoragePicture(
            storageInfo.itemImage,
            binding.ivStorageItem
        )
        binding.tvCurrentlyStoring.text = storageInfo.item
        binding.tvItemTypes.text = storageInfo.itemTypes
        binding.maxCapacity.text = "${storageInfo.maximumCapacity} items"

        val sortedAccessHistory = beneficiaryAccessList.sortedByDescending { it.lastVisited }

        binding.rvFoodBankAccessHistory.layoutManager = LinearLayoutManager(activity)
        binding.rvFoodBankAccessHistory.setHasFixedSize(true)
        val accessHistoryListAdapter =
            AccessHistoryListAdapter(requireActivity(), sortedAccessHistory, this)
        binding.rvFoodBankAccessHistory.adapter = accessHistoryListAdapter
    }


}