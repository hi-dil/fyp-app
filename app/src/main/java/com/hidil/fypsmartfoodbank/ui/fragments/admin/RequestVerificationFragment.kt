package com.hidil.fypsmartfoodbank.ui.fragments.admin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.admin.RequestVerificationClaimViewModel

class RequestVerificationFragment : Fragment() {

    companion object {
        fun newInstance() = RequestVerificationFragment()
    }

    private lateinit var viewModel: RequestVerificationClaimViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_verification_claim, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RequestVerificationClaimViewModel::class.java)
        // TODO: Use the ViewModel
    }

}