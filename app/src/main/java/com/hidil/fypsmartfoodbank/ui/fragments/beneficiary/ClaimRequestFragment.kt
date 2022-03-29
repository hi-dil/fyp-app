package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hidil.fypsmartfoodbank.databinding.FragmentClaimRequestBinding
import com.hidil.fypsmartfoodbank.viewModel.beneficiary.ClaimRequestViewModel

class ClaimRequestFragment : Fragment() {

    private var _binding: FragmentClaimRequestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[ClaimRequestViewModel::class.java]

        _binding = FragmentClaimRequestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}