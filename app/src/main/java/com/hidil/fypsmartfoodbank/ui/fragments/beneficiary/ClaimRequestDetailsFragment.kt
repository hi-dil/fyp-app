package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentClaimRequestDetailsBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.PendingTakeItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.RequestedItemListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClaimRequestDetailsFragment : Fragment() {
    private var _binding: FragmentClaimRequestDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ClaimRequestDetailsFragmentArgs>()
    private lateinit var itemList: ArrayList<ItemList>
    private var takeItemButton = false
    private lateinit var currentRequest: Request

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClaimRequestDetailsBinding.inflate(inflater, container, false)
        itemList = args.currentRequest.items
        currentRequest = args.currentRequest

        GlideLoader(requireContext()).loadUserPicture(
            currentRequest.foodBankImage,
            binding.ivHeader
        )
        binding.tvTitle.text = currentRequest.foodBankName
        binding.tvAddress.text = currentRequest.address

        binding.rvItems.layoutManager = LinearLayoutManager(activity)
        binding.rvItems.setHasFixedSize(true)
        val itemListAdatper = RequestedItemListAdapter(requireActivity(), currentRequest.items)
        binding.rvItems.adapter = itemListAdatper

        if (currentRequest.cancel) {
            binding.tvFailureTag.visibility = View.VISIBLE
        }

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnInProgress.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Please claim all items to complete the request",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnComplete.setOnClickListener {
            Toast.makeText(requireContext(), "You have completed the request", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btnCancelRequest.setOnClickListener {
            cancelRequest()
        }

        binding.btnTakeItem.setOnClickListener {
            val itemStatusAdapter = PendingTakeItemListAdapter(
                requireActivity(),
                currentRequest.items,
                currentRequest
            )
            binding.rvItems.adapter = itemStatusAdapter
            takeItemButton = true
            binding.btnTakeItem.visibility = View.GONE
            binding.btnInProgress.visibility = View.VISIBLE
        }

        if (currentRequest.approved && !currentRequest.completed) {
            binding.tvProgress.text = "verified"
            binding.tvProgress.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.complete_tag)
            binding.btnTakeItem.visibility = View.VISIBLE
        }

        if (currentRequest.completed) {
            binding.btnCancelRequest.visibility = View.GONE
            binding.btnComplete.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (takeItemButton) {
            binding.btnInProgress.visibility = View.VISIBLE
            binding.btnTakeItem.visibility = View.GONE
        }
        if (args.currentRequest.approved && !args.currentRequest.completed && takeItemButton) {
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val data = DatabaseRepo().searchRequestAsync(
                        this@ClaimRequestDetailsFragment,
                        currentRequest.id
                    )
                    if (data.size > 0) {
                        currentRequest = data[0]
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error while refreshing list",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    requireActivity().runOnUiThread {
                        val itemStatusAdapter =
                            PendingTakeItemListAdapter(
                                requireActivity(),
                                currentRequest.items,
                                currentRequest
                            )
                        binding.rvItems.adapter = itemStatusAdapter
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cancelRequest() {
        val views = View.inflate(requireContext(), R.layout.alert_dialog_confirm_cancel, null)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(views)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        views.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        views.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            requireActivity().showProgressDialog()
            currentRequest.cancel = true
            currentRequest.completed = true
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val getFoodBank = DatabaseRepo().searchFoodBank(this@ClaimRequestDetailsFragment, currentRequest.foodBankID)
                    var foodbankData: FoodBank = FoodBank()
                    if (getFoodBank.size > 0){
                        foodbankData = getFoodBank[0]
                    }

                    for (storageItem in foodbankData.storage) {
                        for (requestItem in currentRequest.items) {
                            if (storageItem.id == requestItem.storageID) {
                                storageItem.itemQuantity += requestItem.itemQuantity
                            }
                        }
                    }

                    val updateRequest = DatabaseRepo().updateUserClaimRequest(
                        currentRequest,
                        this@ClaimRequestDetailsFragment
                    )

                    val updateFoodBank =
                        DatabaseRepo().updateFoodBank(this@ClaimRequestDetailsFragment, foodbankData)

                    requireActivity().runOnUiThread {
                        requireActivity().hideProgressDialog()

                        val viewsUpdate =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        val builderUpdate = AlertDialog.Builder(requireActivity())
                        builderUpdate.setView(viewsUpdate)
                        val dialogUpdate = builderUpdate.create()
                        dialogUpdate.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        if (updateRequest && updateFoodBank) {

                            viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                "Your request has been cancelled"

                            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                findNavController().navigate(
                                    ClaimRequestDetailsFragmentDirections.actionClaimRequestDetailsFragmentToDashboardFragment()
                                )
                                dialogUpdate.dismiss()
                            }
                        } else {
                            viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                "There was an error while cancelling the request"

                            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                dialogUpdate.dismiss()
                            }
                        }
                        dialogUpdate.show()
                    }
                }
            }
            dialog.dismiss()
        }
    }
}
