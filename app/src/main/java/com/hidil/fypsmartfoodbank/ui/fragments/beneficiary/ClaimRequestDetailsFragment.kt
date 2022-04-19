package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentClaimRequestDetailsBinding
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.PendingTakeItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.RequestedItemListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.beneficiary.ClaimRequestDetailsViewModel

class ClaimRequestDetailsFragment : Fragment() {
    private var _binding: FragmentClaimRequestDetailsBinding? = null
    private lateinit var viewModel: ClaimRequestDetailsViewModel
    private val binding get() = _binding!!
    private val args by navArgs<ClaimRequestDetailsFragmentArgs>()
    private lateinit var itemList: ArrayList<ItemList>
    private var takeItemButton = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val claimRequestViewModel = ViewModelProvider(this).get(ClaimRequestDetailsViewModel::class.java)
        _binding = FragmentClaimRequestDetailsBinding.inflate(inflater, container, false)
        itemList = args.currentRequest.items

        GlideLoader(requireContext()).loadUserPicture(args.currentRequest.foodBankImage, binding.ivHeader)
        binding.tvTitle.text = args.currentRequest.foodBankName
        binding.tvAddress.text = args.currentRequest.address

        binding.rvItems.layoutManager = LinearLayoutManager(activity)
        binding.rvItems.setHasFixedSize(true)
        val itemListAdatper = RequestedItemListAdapter(requireActivity(), args.currentRequest.items)
        binding.rvItems.adapter = itemListAdatper

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnUserAction.setOnClickListener {
            Toast.makeText(requireContext(), "You must wait for approval before claim the food bank items", Toast.LENGTH_SHORT).show()
        }

        if (args.currentRequest.approved) {
            binding.tvProgress.text = "verified"
            binding.tvProgress.background = ContextCompat.getDrawable(requireContext(), R.drawable.complete_tag)
            binding.btnUserAction.text = "take item"
            binding.btnUserAction.background.setTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))

            binding.btnUserAction.setOnClickListener {
                val itemStatusAdapter = PendingTakeItemListAdapter(requireActivity(), args.currentRequest.items, args.currentRequest)
                binding.rvItems.adapter = itemStatusAdapter
                takeItemButton = true
                binding.btnUserAction.background.setTint(ContextCompat.getColor(requireContext(), R.color.gray))
                binding.btnUserAction.text = "In Progress"
                binding.btnUserAction.setOnClickListener {
                    Toast.makeText(requireContext(), "Please claim all items to complete the request", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (args.currentRequest.completed) {
            binding.btnUserAction.text = "complete"
            binding.btnUserAction.setOnClickListener {
                Toast.makeText(requireContext(), "You already claim the items for this request", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (args.currentRequest.approved && !args.currentRequest.completed && takeItemButton) {
            DatabaseRepo().searchRequest(this, args.currentRequest.id)
            Toast.makeText(requireContext(), "fragment continued", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshList(updatedList: ArrayList<ItemList>){
        val itemStatusAdapter = PendingTakeItemListAdapter(requireActivity(), updatedList, args.currentRequest)
        binding.rvItems.adapter = itemStatusAdapter
    }

}