package com.hidil.fypsmartfoodbank.ui.fragments

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.QRScannerFragmentBinding
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.viewModel.QRScannerViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class QRScannerFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: QRScannerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = QRScannerFragmentBinding.inflate(inflater, container, false)
        requestCameraPermission()

        if (hasCameraPermission()) {
            startScanning()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(Constants.CAMERA_REQUEST_CODE, permissions, grantResults, this)
    }

    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)

    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application needs camera permission",
            Constants.CAMERA_REQUEST_CODE,
            Manifest.permission.CAMERA
        )
    }

    private fun startScanning() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = true
        codeScanner.scanMode = ScanMode.SINGLE

        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread{
               Toast.makeText(requireContext(), "Scan Results: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Camera initialization error: ${it.message}", Toast.LENGTH_SHORT).show()

            }
        }
        (activity as BeneficiaryMainActivity).hideBottomNavigationView()
        codeScanner.startPreview()

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestCameraPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show()
    }

}