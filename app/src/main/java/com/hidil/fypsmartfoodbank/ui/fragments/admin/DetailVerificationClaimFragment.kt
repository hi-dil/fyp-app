package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentDetailVerificationClaimBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.model.RealtimeDBPIN
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.RealtimeDBRepo
import com.hidil.fypsmartfoodbank.repository.RetrofitInstance
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.admin.ClaimRequestDetailsListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.utils.NotificationData
import com.hidil.fypsmartfoodbank.utils.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DetailVerificationClaimFragment : Fragment() {
    private var _binding: FragmentDetailVerificationClaimBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailVerificationClaimFragmentArgs>()
    private lateinit var currentRequest: Request

    private lateinit var userDetails: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailVerificationClaimBinding.inflate(inflater, container, false)

        currentRequest = args.currentRequest

        GlideLoader(requireContext()).loadUserPicture(currentRequest.userImage, binding.ivUserImage)
        binding.tvUserName.text = currentRequest.userName
        binding.tvMobileNumber.text = currentRequest.userMobile

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                userDetails = DatabaseRepo().getAnotherUserDetails(currentRequest.userID)
                requireActivity().runOnUiThread {
                    if (userDetails.isVerified) {
                        binding.ivVerified.visibility = View.VISIBLE
                        binding.cbVerifiedBeneficiary.isChecked = true
                    }
                }
            }
        }

        val dateFormat = "dd MMMM yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = currentRequest.requestDate
        val date = formatter.format(calendar.time)
        binding.tvRequestDate.text = date

        binding.tvFbName.text = currentRequest.foodBankName
        binding.tvFbAddress.text = currentRequest.address
        GlideLoader(requireContext()).loadFoodBankPicture(
            currentRequest.foodBankImage,
            binding.ivFoodBankImage
        )
        binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${currentRequest.lat},${currentRequest.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        }

        binding.rvRequestedItem.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRequestedItem.setHasFixedSize(true)
        val adapter = ClaimRequestDetailsListAdapter(requireContext(), currentRequest.items)
        binding.rvRequestedItem.adapter = adapter

        binding.fabBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnVerify.setOnClickListener {

            val views = View.inflate(requireContext(), R.layout.alert_dialog_confirm_approve, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            views.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            views.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                approveRequest()
                dialog.dismiss()
            }
        }

        binding.btnDeny.setOnClickListener {
            val views = View.inflate(requireContext(), R.layout.alert_dialog_deny_request, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            views.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            views.findViewById<Button>(R.id.btn_denyRequest).setOnClickListener {
                val reason = views.findViewById<TextInputEditText>(R.id.et_reason).text.toString()
                denyRequest(reason)
                dialog.dismiss()
            }
        }
        return binding.root
    }

    // generate a random number from 0 to 999999 with "000000" / 6 digit format
    // it a recursive function - it will repeat until the digit value doesnt match with the database value
    private suspend fun generateRand(storageID: String): String {
        val rnd = Random()
        val number = rnd.nextInt(999999)
        val numberString = String.format("%06d", number)
        var finalNumber = ""
        var listOfPin = HashMap<String, Any>()

        return withContext(Dispatchers.Default) {
            listOfPin = RealtimeDBRepo().getListOfPin(storageID)
            Log.i("listOfPinCoroutine", listOfPin.toString())

            for (key in listOfPin.keys) {
                if (numberString == key) {
                    generateRand(storageID)
                } else {
                    finalNumber = numberString
                    Log.i("pinNumber", finalNumber)
                }
            }

            return@withContext finalNumber
        }
    }

    // run fun to approve user's request
    private fun approveRequest() {
        // generate the pin number
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                for (i in currentRequest.items) {
                    val pinNumber = generateRand(i.storageID)
                    i.storagePIN = pinNumber
                }
            }

            if (binding.cbVerifiedBeneficiary.isChecked != userDetails.isVerified) {
                userDetails.isVerified = binding.cbVerifiedBeneficiary.isChecked
                DatabaseRepo().updateUserData(currentRequest.userID, userDetails)
            }

            requireActivity().runOnUiThread {
                requireActivity().showProgressDialog()
            }

            // update user's request
            currentRequest.approved = true
            currentRequest.lastUpdate = System.currentTimeMillis()

            // will be used to check if the async function return error
            var updateRD = false
            var updateRequest = false

            for (i in currentRequest.items) {
                val pinData =
                    RealtimeDBPIN(System.currentTimeMillis(), currentRequest.id, "claim")
                updateRD = RealtimeDBRepo().updatePin(
                    i.storageID,
                    pinData,
                    i.storagePIN,
                    this@DetailVerificationClaimFragment
                )
                updateRequest = DatabaseRepo().updateUserClaimRequest(
                    currentRequest,
                    this@DetailVerificationClaimFragment
                )
            }

            requireActivity().runOnUiThread {
                requireActivity().hideProgressDialog()
                val views =
                    View.inflate(requireContext(), R.layout.alert_dialog_complete_request, null)
                val builder = AlertDialog.Builder(requireActivity())
                builder.setView(views)
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                    dialog.dismiss()
                }

                // check if the request successfully update to firestore and rdbs
                if (updateRD && updateRequest) {
                    val title = "Request Update"
                    val message = "Your request has been approved"

                    PushNotification(
                        NotificationData(title, message),
                        userDetails.tokenID
                    ).also { sendNotification(it) }

                    views.findViewById<TextView>(R.id.tv_text).text =
                        "Successfully approve user's request"

                    views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                        findNavController().navigate(
                            DetailVerificationClaimFragmentDirections.actionDetailVerificationClaimFragmentToDashboardAdminFragment()
                        )
                        dialog.dismiss()
                    }

                    dialog.show()
                } else {
                    views.findViewById<TextView>(R.id.tv_text).text =
                        "Fail to update user's request"
                    dialog.show()
                }
            }
        }

    }

    private fun denyRequest(reason: String) {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                currentRequest.denied = true
                currentRequest.deniedMessage = reason
                currentRequest.completed = true
                currentRequest.lastUpdate = System.currentTimeMillis()

                val getFoodBank = DatabaseRepo().searchFoodBank(
                    this@DetailVerificationClaimFragment,
                    currentRequest.foodBankID
                )
                var foodbankData: FoodBank = FoodBank()
                if (getFoodBank.size > 0) {
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
                    this@DetailVerificationClaimFragment
                )

                val updateFoodBank =
                    DatabaseRepo().updateFoodBank(
                        this@DetailVerificationClaimFragment,
                        foodbankData
                    )

                val userDetails = DatabaseRepo().getAnotherUserDetails(currentRequest.userID)

                requireActivity().runOnUiThread {

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
                        val title = "Request Update"
                        val message = "Your request has been deny"

                        PushNotification(
                            NotificationData(title, message),
                            userDetails.tokenID
                        ).also { sendNotification(it) }

                        viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                            "Successfully deny user request"

                        viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialogUpdate.dismiss()
                        }
                    } else {
                        viewsUpdate.findViewById<TextView>(R.id.tv_text).text =
                            "Fail to deny user's request"

                        viewsUpdate.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialogUpdate.dismiss()
                        }
                    }
                    dialogUpdate.show()
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
//                Log.d(this.javaClass.simpleName.toString(), "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(this.javaClass.simpleName.toString(), response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName.toString(), e.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}