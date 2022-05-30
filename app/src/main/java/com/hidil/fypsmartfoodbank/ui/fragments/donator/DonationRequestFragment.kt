package com.hidil.fypsmartfoodbank.ui.fragments.donator

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDonationRequestBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.DonatorActivity
import com.hidil.fypsmartfoodbank.ui.adapter.donator.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.donator.PastRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DonationRequestFragment : Fragment() {

    private var _binding: FragmentDonationRequestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDonationRequestBinding.inflate(inflater, container, false)

        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val city = sp?.getString(Constants.USER_CITY, "")!!
        val state = sp.getString(Constants.USER_STATE, "")!!
        binding.tvAddress.text = "$city, $state"

        DatabaseRepo().getDonationActiveRequest(this, AuthenticationRepo().getCurrentUserID())
        getPastRequest()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is DonatorActivity) {
            (activity as DonatorActivity?)!!.showBottomNavigationView()
        }
    }

    fun getActiveRequest(activeRequestList: ArrayList<DonationRequest>) {
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

    fun getPastRequest() {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val pastRequestList =
                    DatabaseRepo().getOldCompleteDonationRequest(this@DonationRequestFragment)

                requireActivity().runOnUiThread {
                    if (pastRequestList.size > 0) {
                        binding.rvPastRequest.visibility = View.VISIBLE
                        binding.tvNoPastRequest.visibility = View.GONE

                        binding.rvPastRequest.layoutManager = LinearLayoutManager(activity)
                        binding.rvPastRequest.setHasFixedSize(true)
                        val activeRequestAdapter =
                            PastRequestListAdapter(
                                requireActivity(),
                                pastRequestList,
                                this@DonationRequestFragment
                            )
                        binding.rvPastRequest.adapter = activeRequestAdapter
                    } else {
                        binding.rvPastRequest.visibility = View.GONE
                        binding.tvNoPastRequest.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}