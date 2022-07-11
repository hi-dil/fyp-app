package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentRequestVerificationBinding
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.AdminMainActivity
import com.hidil.fypsmartfoodbank.ui.adapter.admin.DonationRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.admin.RequestListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestVerificationFragment : Fragment() {

    private var _binding: FragmentRequestVerificationBinding? = null
    private val binding get() = _binding!!
    private var role = "Beneficiary"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestVerificationBinding.inflate(inflater, container, false)
        // set the default dropdown as beneficiary
        binding.acRequest.setText("Beneficiary")

        loadRequest()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is AdminMainActivity) (requireActivity() as AdminMainActivity).showBottomNavigationView()

        loadRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadRequest() {
        // load function based on the dropdown items
        if (role == "Beneficiary") {
            loadBeneficiary()
            binding.acRequest.setText("Beneficiary")
        } else if (role == "Donator"){
            loadDonator()
            binding.acRequest.setText("Donator")
        }

        // set the dropdown items
        val userRole = resources.getStringArray(R.array.user_role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_userrole_item, userRole)
        binding.acRequest.setAdapter(arrayAdapter)

        binding.acRequest.setOnItemClickListener { _, _, _, _ ->
            if (binding.acRequest.text.toString() == "Beneficiary") {
                role = "Beneficiary"
                loadBeneficiary()
            } else if (binding.acRequest.text.toString() == "Donator"){
                role = "Donator"
                loadDonator()
            }
        }
    }

    // fetch data from firebase and display the donator's request
    private fun loadDonator() {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val activeRequest = DatabaseRepo().getOldDonationRequest(10)

                requireActivity().runOnUiThread {
                    if (activeRequest.size > 0) {
                        binding.rvActiveClaimRequests.visibility = View.GONE
                        binding.tvNoActiveClaimRequests.visibility = View.GONE
                        binding.rvActiveDonationRequests.visibility = View.VISIBLE
                        binding.tvNoActiveDonationRequests.visibility = View.GONE

                        binding.rvActiveDonationRequests.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvActiveDonationRequests.setHasFixedSize(true)
                        val adapter = DonationRequestListAdapter(requireContext(), activeRequest, this@RequestVerificationFragment)
                        binding.rvActiveDonationRequests.adapter = adapter
                    } else {
                        binding.rvActiveClaimRequests.visibility = View.GONE
                        binding.tvNoActiveClaimRequests.visibility = View.GONE
                        binding.rvActiveDonationRequests.visibility = View.GONE
                        binding.tvNoActiveDonationRequests.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    // fetch data from firebase and display the beneficiary's request
    private fun loadBeneficiary() {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val activeRequest = DatabaseRepo().getOldestClaimRequest(10)

                requireActivity().runOnUiThread {
                    if (activeRequest.size > 0) {
                        binding.rvActiveClaimRequests.visibility = View.VISIBLE
                        binding.tvNoActiveClaimRequests.visibility = View.GONE
                        binding.rvActiveDonationRequests.visibility = View.GONE
                        binding.tvNoActiveDonationRequests.visibility = View.GONE

                        binding.rvActiveClaimRequests.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvActiveClaimRequests.setHasFixedSize(true)
                        val adapter = RequestListAdapter(requireContext(), activeRequest, this@RequestVerificationFragment)
                        binding.rvActiveClaimRequests.adapter = adapter
                    } else {
                        binding.rvActiveClaimRequests.visibility = View.GONE
                        binding.tvNoActiveClaimRequests.visibility = View.VISIBLE
                        binding.rvActiveDonationRequests.visibility = View.GONE
                        binding.tvNoActiveDonationRequests.visibility = View.GONE
                    }
                }
            }
        }
    }
}