package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentUnlockOptionsDonatorBinding
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.UnlockOptionsFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.donator.UnlockOptionsDonatorViewModel

class UnlockOptionsDonatorFragment : Fragment() {

    private var _binding: FragmentUnlockOptionsDonatorBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UnlockOptionsDonatorFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUnlockOptionsDonatorBinding.inflate(inflater, container, false)


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
                UnlockOptionsDonatorFragmentDirections.actionUnlockOptionsDonatorFragmentToQRScannerDonatorFragment(
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
                args.currentItemListRequest.storagePIN.toString()
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