package com.hidil.fypsmartfoodbank.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentUserInfoBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.PastRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        DatabaseRepo().searchUser(this, args.userID)
        DatabaseRepo().getActiveRequest(this, args.userID)
        DatabaseRepo().getPastRequest(this, args.userID)

        binding.fabBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getUserInfo(userInfo: User) {
        GlideLoader(requireContext()).loadUserPicture(userInfo.image, binding.ivUserImage)
        binding.tvEmail.text = userInfo.email
        binding.tvUserName.text = userInfo.name
        binding.tvUserRole.text = userInfo.userRole
        binding.tvMonthlyIncome.text = userInfo.monthlyIncome
        binding.tvPhoneNumber.text = userInfo.mobileNumber
        binding.btnContact.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel: ${userInfo.mobileNumber}")
            startActivity(dialIntent)
        }
    }


    fun getActiveRequest(activeRequestList: ArrayList<Request>) {
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

    fun getPastRequest(pastRequestList: ArrayList<Request>) {
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