package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
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
import java.util.concurrent.TimeUnit

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
    ): View {
        _binding = FragmentClaimRequestDetailsBinding.inflate(inflater, container, false)
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

                        if (updateDatabase) {
                            requireActivity().runOnUiThread {
                                binding.llTimeLeft.visibility = View.GONE
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
            currentRequest.foodBankImage,
            binding.ivHeader
        )
        binding.tvTitle.text = currentRequest.foodBankName
        binding.tvAddress.text = currentRequest.address

        binding.rvItems.layoutManager = LinearLayoutManager(activity)
        binding.rvItems.setHasFixedSize(true)
        val itemListAdapter = RequestedItemListAdapter(requireActivity(), currentRequest.items)
        binding.rvItems.adapter = itemListAdapter

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
            binding.tvApproveTag.visibility = View.VISIBLE
            binding.tvPendingTag.visibility = View.GONE
            binding.btnTakeItem.visibility = View.VISIBLE
        }

        binding.btnShowReason.setOnClickListener {
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

            viewsUpdate.findViewById<TextView>(R.id.tv_text).text = currentRequest.deniedMessage
            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialogUpdate.dismiss()
            }
            dialogUpdate.show()
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (takeItemButton) {
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

                    var complete = false
                    for (storage in data[0].items) {
                        complete = storage.completed
                    }
                    Log.i("isComplete", complete.toString())

                    if (complete) {
                        currentRequest.completed = complete
                        val updateRequest = DatabaseRepo().updateUserClaimRequest(currentRequest, this@ClaimRequestDetailsFragment)

                        if (updateRequest) {
                            val updatedData = DatabaseRepo().searchRequestAsync(this@ClaimRequestDetailsFragment, currentRequest.id)
                            if (updatedData.size > 0) {
                                requireActivity().runOnUiThread {
                                    findNavController().navigate(ClaimRequestDetailsFragmentDirections.actionClaimRequestDetailsFragmentSelf(updatedData[0]))
                                }
                            }
                        }
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
            currentRequest.lastUpdate = System.currentTimeMillis()
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {

                    val cancelRequestUpdate = updateDatabase()


                    requireActivity().runOnUiThread {
                        hideProgressDialog()

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

                        if (cancelRequestUpdate) {

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

    private suspend fun updateDatabase(): Boolean {
        // fetch food bank data from firebase
        val getFoodBank = DatabaseRepo().searchFoodBank(
            this@ClaimRequestDetailsFragment,
            currentRequest.foodBankID
        )
        var foodbankData = FoodBank()
        if (getFoodBank.size > 0) {
            foodbankData = getFoodBank[0]
        }

        // to get the status of the deleted pin
        var successDeletePin = false
        for (storageItem in foodbankData.storage) {
            for (requestItem in currentRequest.items) {

                // increase the storage quantity to its original state
                if (storageItem.id == requestItem.storageID) {
                    if (!requestItem.completed) {
                        storageItem.itemQuantity += requestItem.itemQuantity
                    }
                }

                // delete the pin from the realtime database
                if (!requestItem.completed && currentRequest.approved) {
                    val storageID = requestItem.storageID
                    val storagePIN = requestItem.storagePIN
                    successDeletePin = RealtimeDBRepo().deletePin(storagePIN, storageID)
                }
            }
        }

        // update the cancel request data to firebase
        val updateRequest = DatabaseRepo().updateUserClaimRequest(
            currentRequest,
            this@ClaimRequestDetailsFragment
        )

        val updateFoodBank =
            DatabaseRepo().updateFoodBank(
                this@ClaimRequestDetailsFragment,
                foodbankData
            )

        return successDeletePin && updateRequest && updateFoodBank
    }
}
