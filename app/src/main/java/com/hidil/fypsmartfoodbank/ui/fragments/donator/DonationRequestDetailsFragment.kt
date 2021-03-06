package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentDonationRequestDetailsBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.donator.DonationRequestedItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.PendingTakeItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ShowImageLinkAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DonationRequestDetailsFragment : Fragment() {

    private var _binding: FragmentDonationRequestDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DonationRequestDetailsFragmentArgs>()
    private lateinit var itemList: ArrayList<ItemListDonation>
    private var donateItemButton = false
    private lateinit var currentRequest: DonationRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonationRequestDetailsBinding.inflate(inflater, container, false)
        itemList = args.currentRequest.items
        currentRequest = args.currentRequest


        // convert millis to hours and minutes
        if (!currentRequest.denied && currentRequest.approved) {
            var timeLeft = (currentRequest.lastUpdate + 86400000) - System.currentTimeMillis()
            if (timeLeft > 0) {
                val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                timeLeft -= TimeUnit.HOURS.toMillis(hours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft)

                binding.tvTimeLeft.text = "$hours hours and $minutes minutes left"
            } else {
                currentRequest.denied = true
                currentRequest.deniedMessage = "Expired request"
                currentRequest.completed = true

                CoroutineScope(IO).launch {
                    withContext(Dispatchers.Default) {

                        val updateDatabase = updateDatabase()

                        requireActivity().runOnUiThread {
                            if (updateDatabase) {
                                binding.llTimeLeft.visibility = View.GONE

                                val viewsUpdate =
                                    View.inflate(
                                        requireContext(),
                                        R.layout.alert_dialog_complete_request,
                                        null
                                    )
                                val builderUpdate =
                                    android.app.AlertDialog.Builder(requireActivity())
                                builderUpdate.setView(viewsUpdate)
                                val dialogUpdate = builderUpdate.create()
                                dialogUpdate.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                dialogUpdate.show()

                                viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                    "Your request has been expired"

                                viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                    dialogUpdate.dismiss()
                                }
                            }

                        }

                    }
                }
            }
        } else {
            binding.llTimeLeft.visibility = View.GONE
        }

        GlideLoader(requireContext()).loadUserPicture(
            args.currentRequest.foodBankImage,
            binding.ivHeader
        )
        binding.tvTitle.text = args.currentRequest.foodBankName
        binding.tvAddress.text = args.currentRequest.address

        binding.rvItems.layoutManager = LinearLayoutManager(activity)
        binding.rvItems.setHasFixedSize(true)

        val itemListAdapter =
            DonationRequestedItemListAdapter(requireActivity(), args.currentRequest.items, this)
        binding.rvItems.adapter = itemListAdapter

        binding.rvRequestImage.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRequestImage.setHasFixedSize(true)
        val adapter = ShowImageLinkAdapter(requireContext(), args.currentRequest.requestImages)
        binding.rvRequestImage.adapter = adapter

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (currentRequest.cancel) {
            binding.btnCancelRequest.visibility = View.GONE
            binding.tvCancelTag.visibility = View.VISIBLE
            binding.tvPendingTag.visibility = View.GONE
        } else if (currentRequest.denied) {
            binding.tvDenyTag.visibility = View.VISIBLE
            binding.tvPendingTag.visibility = View.GONE
            binding.btnShowReason.visibility = View.VISIBLE
            binding.btnCancelRequest.visibility = View.GONE
        } else if (currentRequest.completed) {
            binding.tvCompleteTag.visibility = View.VISIBLE
            binding.tvPendingTag.visibility = View.GONE
            binding.btnCancelRequest.visibility = View.GONE
        } else if (currentRequest.approved && !currentRequest.completed) {
            binding.tvApproveTag.visibility = View.VISIBLE
            binding.tvPendingTag.visibility = View.GONE
            binding.btnDonateItem.visibility = View.VISIBLE
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

        binding.btnDonateItem.setOnClickListener {
            val itemStatusAdapter =
                PendingTakeItemListAdapter(requireActivity(), currentRequest.items, currentRequest)
            binding.rvItems.adapter = itemStatusAdapter
            donateItemButton = true

            binding.btnDonateItem.visibility = View.GONE
            binding.btnInProgress.visibility = View.VISIBLE
        }

        binding.btnShowReason.setOnClickListener {
            val viewsUpdate =
                View.inflate(
                    requireContext(),
                    R.layout.alert_dialog_complete_request,
                    null
                )
            val builderUpdate = android.app.AlertDialog.Builder(requireActivity())
            builderUpdate.setView(viewsUpdate)
            val dialogUpdate = builderUpdate.create()
            dialogUpdate.window?.setBackgroundDrawableResource(android.R.color.transparent)

            viewsUpdate.findViewById<TextView>(R.id.tv_text).text = currentRequest.deniedMessage
            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialogUpdate.dismiss()
            }
            dialogUpdate.show()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (donateItemButton) {
            binding.btnDonateItem.visibility = View.GONE
        }

        if (args.currentRequest.approved && !args.currentRequest.completed && donateItemButton) {
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    val data = DatabaseRepo().searchDonationRequestAsync(
                        this@DonationRequestDetailsFragment,
                        currentRequest.id
                    )
                    if (data.size > 0) {
                        currentRequest = data[0]
                    }

                    var complete = false
                    for (storage in data[0].items) {
                        complete = storage.completed
                    }

                    if (complete) {
                        currentRequest.completed = complete
                        val updateRequest = DatabaseRepo().updateUserDonationRequest(
                            currentRequest,
                            this@DonationRequestDetailsFragment
                        )

                        if (updateRequest) {
                            val updateData = DatabaseRepo().searchDonationRequestAsync(
                                this@DonationRequestDetailsFragment,
                                currentRequest.id
                            )

                            if (updateData.size > 0) {
                                requireActivity().runOnUiThread {
                                    findNavController().navigate(
                                        DonationRequestDetailsFragmentDirections.actionDonationRequestDetailsFragmentSelf(
                                            updateData[0]
                                        )
                                    )
                                }
                            }

                        }
                    }

                    requireActivity().runOnUiThread {
                        val itemStatusAdapter = PendingTakeItemListAdapter(
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
            currentRequest.lastUpdate = System.currentTimeMillis()

            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {

                    val updateDatabase = updateDatabase()
                    requireActivity().runOnUiThread {
                        hideProgressDialog()

                        val viewsUpdate =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        val builderUpdate = android.app.AlertDialog.Builder(requireActivity())
                        builderUpdate.setView(viewsUpdate)
                        val dialogUpdate = builderUpdate.create()
                        dialogUpdate.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        if (updateDatabase) {

                            viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                "Your request has been cancelled"

                            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                findNavController().navigate(
                                    DonationRequestDetailsFragmentDirections.actionDonationRequestDetailsFragmentToDashboardDonatorFragment()
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

    private suspend fun updateDatabase(): Boolean {
        // to get the status of the deleted pin
        var successDeletePin = false
        for (requestItem in currentRequest.items) {

            // delete the pin from the realtime database
            if (!requestItem.completed && currentRequest.approved) {
                val storageID = requestItem.storageID
                val storagePIN = requestItem.storagePIN
                successDeletePin = RealtimeDBRepo().deletePin(storagePIN, storageID)
            }
        }

        val updateRequest = DatabaseRepo().updateUserDonationRequest(
            currentRequest, this@DonationRequestDetailsFragment
        )

        Log.i("successDeletePin", successDeletePin.toString())

        return successDeletePin && updateRequest
    }
}