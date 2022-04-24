package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.ConfirmRequestFragmentBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ClaimRequestItemListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.ConfimRequestViewModel

class ConfirmRequestFragment : Fragment() {

    private var _binding: ConfirmRequestFragmentBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ConfirmRequestFragmentArgs>()
    private lateinit var activeRequest: ArrayList<Request>

    private lateinit var viewModel: ConfimRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmRequestFragmentBinding.inflate(inflater, container, false)

        DatabaseRepo().getActiveRequest(this)

        binding.tvFbName.text = args.proposedRequest.foodBankName
        binding.tvFbAddress.text = args.proposedRequest.address
        binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${args.proposedRequest.lat},${args.proposedRequest.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        }

        var totalItems = 0
        for (i in args.proposedRequest.items) {
            totalItems += i.itemQuantity
        }

        GlideLoader(requireContext()).loadFoodBankPicture(
            args.proposedRequest.foodBankImage,
            binding.ivFoodBankImage
        )

        binding.tvTotalAmount.text = totalItems.toString()
        binding.tvAddMore.text = "You can add ${20 - totalItems} more items to your request"

        binding.rvRequestedItem.layoutManager = LinearLayoutManager(activity)
        binding.rvRequestedItem.setHasFixedSize(true)
        val itemListAdapter =
            ClaimRequestItemListAdapter(requireActivity(), args.proposedRequest.items, this)
        binding.rvRequestedItem.adapter = itemListAdapter

        binding.btnConfirmRequest.setOnClickListener {
            if (activeRequest.size > 0) {
                Toast.makeText(
                    requireContext(),
                    "You already have an active request",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val request = Request(
                    "",
                    completed = false,
                    approved = false,
                    foodBankImage = args.proposedRequest.foodBankImage,
                    foodBankID = args.proposedRequest.foodBankID,
                    foodBankName = args.proposedRequest.foodBankName,
                    requestDate = System.currentTimeMillis(),
                    lastUpdate = System.currentTimeMillis(),
                    userID = args.proposedRequest.userID,
                    items = args.proposedRequest.items,
                    address = args.proposedRequest.address,
                    lat = args.proposedRequest.lat,
                    long = args.proposedRequest.long
                )

                DatabaseRepo().saveRequest(this, request)
            }


        }

        return binding.root
    }

    fun assignActiveRequest(request: ArrayList<Request>) {
        activeRequest = request
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}