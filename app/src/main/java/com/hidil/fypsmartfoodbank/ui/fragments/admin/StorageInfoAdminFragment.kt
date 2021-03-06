package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentStorageInfoAdminBinding
import com.hidil.fypsmartfoodbank.model.Storage
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
import com.hidil.fypsmartfoodbank.ui.adapter.AccessHistoryListAdapter
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragmentArgs
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StorageInfoAdminFragment : Fragment() {
    private var _binding: FragmentStorageInfoAdminBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StorageInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageInfoAdminBinding.inflate(inflater, container, false)
        DatabaseRepo().searchStorageDetails(this, args.storageID)
        CoroutineScope(Dispatchers.IO).launch {
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
        binding.tvTitle.text = storageInfo.storageName
        GlideLoader(requireContext()).loadStoragePicture(
            storageInfo.itemImage,
            binding.ivStorageItem
        )
        binding.tvCurrentlyStoring.text = storageInfo.item
        binding.tvItemTypes.text = storageInfo.itemTypes
        binding.maxCapacity.text = "${storageInfo.maximumCapacity} items"

        // sort the access history by date
        val sortedAccessHistory = storageInfo.accessHistory.sortedByDescending { it.lastVisited }

        binding.rvFoodBankAccessHistory.layoutManager = LinearLayoutManager(activity)
        binding.rvFoodBankAccessHistory.setHasFixedSize(true)
        val accessHistoryListAdapter =
            AccessHistoryListAdapter(
                requireActivity(),
                sortedAccessHistory, this
            )
        binding.rvFoodBankAccessHistory.adapter = accessHistoryListAdapter


    }
}