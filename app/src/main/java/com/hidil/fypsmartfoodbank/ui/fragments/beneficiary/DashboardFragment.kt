package com.hidil.fypsmartfoodbank.ui.fragments.beneficiary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.databinding.FragmentDashboardBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.activity.hideProgressDialog
import com.hidil.fypsmartfoodbank.ui.activity.showProgressDialog
import com.hidil.fypsmartfoodbank.ui.adapter.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import com.hidil.fypsmartfoodbank.viewModel.beneficiary.DashboardViewModel


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sp = activity?.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE)
        val name = sp?.getString(Constants.LOGGED_IN_USER, "")!!
        val userImage = sp.getString(Constants.USER_PROFILE_IMAGE, "")!!
        val city = sp.getString(Constants.USER_CITY, "")!!
        val state = sp.getString(Constants.USER_STATE, "")!!

        binding.tvUserGreeting.text = "Welcome back $name"
        binding.tvAddress.text = "$city, $state"
        GlideLoader(requireContext()).loadUserPicture(userImage, binding.ivUserProfile)
        requireActivity().showProgressDialog()
        DatabaseRepo().getActiveRequest(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successRequestFromFirestore(activeRequestList: ArrayList<Request>) {
        requireActivity().hideProgressDialog()

        if (activeRequestList.size > 0) {
            binding.rvActiveRequest.visibility = View.VISIBLE
            binding.tvNoActiveRequest.visibility = View.GONE

            binding.rvActiveRequest.layoutManager = LinearLayoutManager(activity)
            binding.rvActiveRequest.setHasFixedSize(true)
            val activeRequestAdapter = ActiveRequestListAdapter(requireActivity(), activeRequestList)
            binding.rvActiveRequest.adapter = activeRequestAdapter
        } else {
            binding.rvActiveRequest.visibility = View.GONE
            binding.tvNoActiveRequest.visibility = View.VISIBLE
        }

        for (i in activeRequestList) {
            Log.i("Food Bank Name", i.foodBankName)
            Log.i("Food Bank Image", i.foodBankImage)
            Log.i("Food Bank ID", i.foodBankID)
            Log.i("User ID", i.userID)
        }
    }
}