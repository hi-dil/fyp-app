package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.hidil.fypsmartfoodbank.databinding.FragmentConfirmRequestBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ClaimRequestItemListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmRequestFragment : Fragment() {

    private var _binding: FragmentConfirmRequestBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ConfirmRequestFragmentArgs>()
    private lateinit var activeRequest: ArrayList<Request>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmRequestBinding.inflate(inflater, container, false)

        DatabaseRepo().getActiveRequest(this, AuthenticationRepo().getCurrentUserID())
        val foodbank = args.currentFoodBank

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

        val sp = requireActivity().getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp.getString(Constants.LOGGED_IN_USER, "")
        val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")
        val mobileNumber = sp.getString(Constants.MOBILE_NUMBER, "")

        binding.btnConfirmRequest.setOnClickListener {
            if (activeRequest.size > 0) {
                Toast.makeText(
                    requireContext(),
                    "You already have an active request",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                requireActivity().showProgressDialog()
                val request = Request(
                    "",
                    completed = false,
                    approved = false,
                    cancel = false,
                    denied = false,
                    deniedMessage = "",
                    foodBankImage = args.proposedRequest.foodBankImage,
                    foodBankID = args.proposedRequest.foodBankID,
                    foodBankName = args.proposedRequest.foodBankName,
                    requestDate = System.currentTimeMillis(),
                    lastUpdate = System.currentTimeMillis(),
                    userID = args.proposedRequest.userID,
                    userImage = userImage.toString(),
                    userName = name.toString(),
                    userMobile = mobileNumber.toString(),
                    items = args.proposedRequest.items,
                    address = args.proposedRequest.address,
                    lat = args.proposedRequest.lat,
                    long = args.proposedRequest.long
                )

                for (storageItem in foodbank.storage) {
                    for (requestItem in request.items) {
                        if (storageItem.id == requestItem.storageID) {
                            storageItem.itemQuantity -= requestItem.itemQuantity
                        }
                    }
                }

                CoroutineScope(IO).launch {
                    withContext(Dispatchers.Default) {
                        val saveRequest =
                            DatabaseRepo().saveRequest(this@ConfirmRequestFragment, request)
                        val updateFoodBank =
                            DatabaseRepo().updateFoodBank(this@ConfirmRequestFragment, foodbank)

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

                            if (saveRequest && updateFoodBank) {
                                viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                    "Your request have been saved"
                            } else {
                                viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                                    "There was an error while saving your request"

                                Log.i("saveRequest", saveRequest.toString())
                                Log.i("saveFoodBank", updateFoodBank.toString())
                            }

                            dialogUpdate.show()

                            viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                findNavController().navigate(
                                    ConfirmRequestFragmentDirections.actionConfimRequestFragmentToDashboardFragment()
                                )
                                dialogUpdate.dismiss()
                            }
                        }
                    }
                }
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