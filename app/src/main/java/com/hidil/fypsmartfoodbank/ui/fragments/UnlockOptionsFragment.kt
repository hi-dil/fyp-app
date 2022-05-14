package com.hidil.fypsmartfoodbank.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.UnlockOptionsFragmentBinding
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.UnlockOptionsViewModel

class UnlockOptionsFragment : Fragment() {
    private var _binding: UnlockOptionsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UnlockOptionsViewModel
    private val args by navArgs<UnlockOptionsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UnlockOptionsFragmentBinding.inflate(inflater, container, false)


        GlideLoader(requireContext()).loadUserPicture(
            args.currentRequest.foodBankImage,
            binding.ivHeader
        )
        binding.tvTitle.text = args.currentRequest.foodBankName
        binding.tvAddress.text = args.currentRequest.address
        binding.tvStorageID.text = args.currentRequest.items[args.storagePosition].storageName

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.llScanQR.setOnClickListener {
            view
            view?.findNavController()?.navigate(
                UnlockOptionsFragmentDirections.actionUnlockOptionsFragmentToQRScannerFragment(
                    args.currentRequest.items[args.storagePosition].storageID,
                    args.currentRequest
                )
            )
        }

        binding.llEnterPIN.setOnClickListener {
            val views = View.inflate(requireContext(), R.layout.alert_dialog_pin_number, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
            views.findViewById<TextView>(R.id.tv_PinNumber).text =
                args.currentItemlistRequest.storagePIN.toString()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}