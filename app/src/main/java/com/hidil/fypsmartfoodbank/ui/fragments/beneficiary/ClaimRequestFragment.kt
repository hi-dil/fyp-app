package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentClaimRequestBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.PastRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.viewModel.beneficiary.ClaimRequestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val city = sp?.getString(Constants.USER_CITY, "")!!
        val state = sp.getString(Constants.USER_STATE, "")!!
        binding.tvAddress.text = "$city, $state"

        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                requireActivity().runOnUiThread { requireActivity().showProgressDialog() }
                val activeRequest = DatabaseRepo().getActiveRequestAsync()
                val pastRequest = DatabaseRepo().getPastRequestAsync()

                requireActivity().runOnUiThread {
                    hideProgressDialog()
                    bindActiveRequest(activeRequest)
                    bindPastRequest(pastRequest)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is BeneficiaryMainActivity) {
            (activity as BeneficiaryMainActivity?)!!.showBottomNavigationView()
        }
    }

    private fun bindActiveRequest(activeRequestList: ArrayList<Request>) {
        if (activeRequestList.size > 0) {
            binding.rvActiveRequest.visibility = View.VISIBLE
            binding.tvNoActiveRequest.visibility = View.GONE

            binding.rvActiveRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvActiveRequest.setHasFixedSize(true)
            val activeRequestAdapter =
                ActiveRequestListAdapter(requireActivity(), activeRequestList, this)
            binding.rvActiveRequest.adapter = activeRequestAdapter
        } else {
            binding.rvActiveRequest.visibility = View.GONE
            binding.tvNoActiveRequest.visibility = View.VISIBLE
        }
    }

    private fun bindPastRequest(pastRequestList: ArrayList<Request>) {
        if (pastRequestList.size > 0) {
            binding.rvPastRequest.visibility = View.VISIBLE
            binding.tvNoPastRequest.visibility = View.GONE

            binding.rvPastRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvPastRequest.setHasFixedSize(true)
            val pastRequestAdapter =
                PastRequestListAdapter(requireActivity(), pastRequestList, this)
            binding.rvPastRequest.adapter = pastRequestAdapter
        } else {
            binding.rvPastRequest.visibility = View.GONE
            binding.tvNoPastRequest.visibility = View.VISIBLE
        }
    }
}