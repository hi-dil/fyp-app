package com.hidil.fypsmartfoodbank.ui.fragments.donator

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.donator.ConfirmDonationRequestViewModel

class ConfirmDonationRequestFragment : Fragment() {

    companion object {
        fun newInstance() = ConfirmDonationRequestFragment()
    }

    private lateinit var viewModel: ConfirmDonationRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_donation_request, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfirmDonationRequestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}