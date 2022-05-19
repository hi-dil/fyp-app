package com.hidil.fypsmartfoodbank.ui.fragments.donator

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
import com.hidil.fypsmartfoodbank.databinding.FragmentDonationRequestDetailsBinding
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.ui.adapter.donator.DonationRequestedItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.PendingTakeItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ShowImageLinkAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class DonationRequestDetailsFragment : Fragment() {

    private var _binding: FragmentDonationRequestDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DonationRequestDetailsFragmentArgs>()
    private lateinit var itemList: ArrayList<ItemListDonation>
    private var donateItemButton = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentDonationRequestDetailsBinding.inflate(inflater, container, false)
        itemList = args.currentRequest.items

        GlideLoader(requireContext()).loadUserPicture(args.currentRequest.foodBankImage, binding.ivHeader)
        binding.tvTitle.text = args.currentRequest.foodBankName
        binding.tvAddress.text = args.currentRequest.address

        binding.rvItems.layoutManager = LinearLayoutManager(activity)
        binding.rvItems.setHasFixedSize(true)

        val itemListAdatper = DonationRequestedItemListAdapter(requireActivity(), args.currentRequest.items, this)
        binding.rvItems.adapter = itemListAdatper

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnUserAction.setOnClickListener {
            Toast.makeText(requireContext(), "You must wait for approval before donate the food bank items", Toast.LENGTH_SHORT).show()
        }

        binding.rvRequestImage.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRequestImage.setHasFixedSize(true)
        val adapter = ShowImageLinkAdapter(requireContext(), args.currentRequest.requestImages)
        binding.rvRequestImage.adapter = adapter

        if (args.currentRequest.approved) {
            binding.tvProgress.text = "verified"
            binding.tvProgress.background = ContextCompat.getDrawable(requireContext(), R.drawable.complete_tag)
            binding.btnUserAction.text = "take item"
            binding.btnUserAction.background.setTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))

            binding.btnUserAction.setOnClickListener {
                val itemStatusAdapter = PendingTakeItemListAdapter(requireActivity(), args.currentRequest.items, args.currentRequest)
                binding.rvItems.adapter = itemStatusAdapter
                donateItemButton = true
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
                Toast.makeText(requireContext(), "You already donate the items for this request", Toast.LENGTH_SHORT).show()
            }
            binding.btnUserAction.background.setTint(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}