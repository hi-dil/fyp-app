package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.viewModel.ConfimRequestViewModel

class ConfimRequestFragment : Fragment() {

    companion object {
        fun newInstance() = ConfimRequestFragment()
    }

    private lateinit var viewModel: ConfimRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confim_request_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfimRequestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}