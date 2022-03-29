package com.hidil.fypsmartfoodbank.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.UserProfileFragmentBinding
import com.hidil.fypsmartfoodbank.repository.AuthenticationRepo
import com.hidil.fypsmartfoodbank.viewModel.UserProfileViewModel

class UserProfileFragment : Fragment() {

    private var _binding: UserProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

        _binding = UserProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogout.setOnClickListener {
            AuthenticationRepo().logout(this)
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}