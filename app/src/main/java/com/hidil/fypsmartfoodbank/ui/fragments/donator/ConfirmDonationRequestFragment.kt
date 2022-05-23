package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentConfirmDonationRequestBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.repository.StorageRepo
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ConfirmItemListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ShowImagesItemListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ConfirmDonationRequestFragment : Fragment() {

    private var _binding: FragmentConfirmDonationRequestBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ConfirmDonationRequestFragmentArgs>()
    private lateinit var currentRequest: DonationRequest
    private val imagesURI: ArrayList<Uri> = ArrayList()
    private val imageURL: ArrayList<String> = ArrayList()

    private var itemListDonation: ArrayList<ItemListDonation> = ArrayList()
    private lateinit var mProgressDialog: Dialog
    private lateinit var mAlertDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmDonationRequestBinding.inflate(inflater, container, false)

        binding.tvFbName.text = args.proposedRequest.foodBankName
        binding.tvFbAddress.text = args.proposedRequest.address
        binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${args.proposedRequest.lat},${args.proposedRequest.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        }

        mAlertDialog = Dialog(requireContext())
        mAlertDialog.setContentView(R.layout.alert_dialog_complete_request)
        mAlertDialog.setCancelable(false)
        mAlertDialog.setCanceledOnTouchOutside(false)
        mAlertDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )


        var totalItems = 0
        for (i in args.proposedRequest.items) {
            totalItems += i.itemQuantity
        }

        GlideLoader(requireContext()).loadFoodBankPicture(
            args.proposedRequest.foodBankImage,
            binding.ivFoodBankImage
        )

        binding.rvRequestImage.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRequestImage.setHasFixedSize(true)

        binding.tvTotalAmount.text = totalItems.toString()

        binding.rvRequestedItem.layoutManager = LinearLayoutManager(activity)
        binding.rvRequestedItem.setHasFixedSize(true)
        val itemListAdapter =
            ConfirmItemListAdapter(requireContext(), args.proposedRequest.items, this)
        binding.rvRequestedItem.adapter = itemListAdapter

        for (i in args.proposedRequest.items) {
            itemListDonation.add(ItemListDonation())
        }

        binding.btnSelectImages.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.chooseMultipleImages(this)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnClearImages.setOnClickListener {
            imagesURI.clear()
            val imagesAdapter = ShowImagesItemListAdapter(requireContext(), imagesURI)
            binding.rvRequestImage.adapter = imagesAdapter
        }

        binding.btnConfirmRequest.setOnClickListener {
            confirmRequest()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mAlertDialogText = view.findViewById(R.id.tv_textadcr)
//        mAlertDialogButton = view.findViewById(R.id.btn_okadcr)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.chooseMultipleImages(this)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {

                // if multiple images are selected
                if (data?.clipData != null) {
                    try {
                        val count = data.clipData?.itemCount

                        if (count != null) {
                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                                imagesURI.add(imageUri)
                                //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            "image selection successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else if (data?.data != null) {
                    try {
                        // if single image is selected
                        val imageUri: Uri = data.data!!
                        //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
                        imagesURI.add(imageUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                val imagesAdapter = ShowImagesItemListAdapter(requireContext(), imagesURI)
                binding.rvRequestImage.adapter = imagesAdapter
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun confirmRequest() {

        var hasEmptyField = false
        for (i in itemListDonation) {
            if (i.itemBrand.isEmpty() || i.expiryDate == 0L) {
                hasEmptyField = true
            }
        }

        if (hasEmptyField) {
            Toast.makeText(
                requireContext(),
                "Please complete the field before confirm your request",
                Toast.LENGTH_SHORT
            ).show()
        } else if (imagesURI.size < args.proposedRequest.items.size) {
            Toast.makeText(
                requireContext(),
                "Please select at least ${args.proposedRequest.items.size} images before completing the request.",
                Toast.LENGTH_LONG
            ).show()
        }

        if (imagesURI.isNotEmpty()) {

            mProgressDialog = Dialog(requireContext())
            mProgressDialog.setContentView(R.layout.alert_dialog)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            mProgressDialog.show()

            CoroutineScope(IO).launch {
                val activeRequest = DatabaseRepo().getActiveRequestDonatorAsync()

//                if (activeRequest.size > 1) {
//                    mProgressDialog.dismiss()
//                    requireActivity().runOnUiThread {
//                        mAlertDialogText.text = "You already have an active request"
//                        mAlertDialog.show()
//                        mAlertDialogButton.setOnClickListener{
//                            mAlertDialog.dismiss()
//                        }
//                    }
//                } else {
                    withContext(Dispatchers.Default) { uploadImages() }
                    uploadRequest()
//                }
            }
        }
    }

    private suspend fun uploadImages() {
        for (i in imagesURI) {
            val link = StorageRepo().uploadImageToCloudStorageFragment(
                this@ConfirmDonationRequestFragment,
                i,
                Constants.ITEM_IMAGE
            )
            imageURL.add(link)
            Log.i("imageLink", imageURL.toString())
        }
    }

    private fun uploadRequest() {
        if (imagesURI.size == imageURL.size) {
            mProgressDialog.dismiss()
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Successfully uploaded all the item",
                    Toast.LENGTH_LONG
                ).show()
            }
            val sp = requireActivity().getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
            val name = sp.getString(Constants.LOGGED_IN_USER, "")
            val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")
            val mobileNumber = sp.getString(Constants.MOBILE_NUMBER, "")

            currentRequest = DonationRequest(
                args.proposedRequest.id,
                false,
                false,
                args.proposedRequest.foodBankImage,
                args.proposedRequest.foodBankID,
                args.proposedRequest.foodBankName,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                AuthenticationRepo().getCurrentUserID(),
                userImage.toString(),
                mobileNumber.toString(),
                name.toString(),
                itemListDonation,
                args.proposedRequest.address,
                args.proposedRequest.lat,
                args.proposedRequest.long,
                imageURL
            )

            val saveStatus = DatabaseRepo().saveRequestDonation(this, currentRequest)

            if (saveStatus) {
                findNavController().popBackStack()
            }
        } else {
            mProgressDialog.dismiss()
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Failed uploading the files", Toast.LENGTH_LONG)
                    .show()
            }

        }
    }

    fun updateItemList(list: ItemListDonation, position: Int) {
        itemListDonation[position] = list
    }
}