package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.FirebaseDatabase
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.UnlockOptionsFragmentBinding
import com.hidil.fypsmartfoodbank.model.ArduinoData
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


        GlideLoader(requireContext()).loadUserPicture(args.currentRequest.foodBankImage, binding.ivHeader)
        binding.tvTitle.text = args.currentRequest.foodBankName
        binding.tvAddress.text = args.currentRequest.address
        binding.tvStorageID.text = args.currentRequest.items[args.storagePosition].storageName

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.llScanQR.setOnClickListener { view
            view?.findNavController()?.navigate(
                UnlockOptionsFragmentDirections.actionUnlockOptionsFragmentToQRScannerFragment()
            )
        }

        binding.llEnterPIN.setOnClickListener {
            val arduinoData = ArduinoData(123.53, "STORAGE 1","SfbClQ6PRR9UKREP5BwO", false)

            val realtimeDatabase = FirebaseDatabase.getInstance("https://smart-foodbank-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("StorageData")
            realtimeDatabase.setValue(arduinoData).addOnSuccessListener {
                Toast.makeText(requireContext(), "successfully saved to database", Toast.LENGTH_SHORT).show()
                Log.i("test", "successfully saved to database")
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save in database", Toast.LENGTH_SHORT).show()
                Log.i("test", "Faield to save in database")
            }


            val views = View.inflate(requireContext(), R.layout.alert_dialog_pin_number, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
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