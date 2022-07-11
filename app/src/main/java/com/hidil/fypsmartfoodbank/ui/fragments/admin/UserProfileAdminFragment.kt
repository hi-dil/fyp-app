package com.hidil.fypsmartfoodbank.ui.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.FragmentUserProfileAdminBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.ActiveRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.adapter.beneficiary.PastRequestListAdapter
import com.hidil.fypsmartfoodbank.ui.fragments.UserInfoFragmentArgs
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileAdminFragment : Fragment() {
    private var _binding: FragmentUserProfileAdminBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserInfoFragmentArgs>()
    private lateinit var userInfo: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileAdminBinding.inflate(inflater, container, false)
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

    fun setUserInfo() {
        if (userInfo.ban) {
            binding.btnBan.visibility = View.GONE
            binding.btnUnban.visibility = View.VISIBLE
        } else {
            binding.btnBan.visibility = View.VISIBLE
            binding.btnUnban.visibility = View.GONE
        }

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

    fun getUserInfo(user: User) {
        userInfo = user
        setUserInfo()

        binding.btnBan.setOnClickListener {
            val views = View.inflate(requireContext(), R.layout.alert_dialog_confirm_approve, null)
            views.findViewById<TextView>(R.id.tv_text).text = "Do you want to ban the user?"
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            views.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            views.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                banUser()
                setUserInfo()
                dialog.dismiss()
            }
        }

        binding.btnUnban.setOnClickListener {
            val views = View.inflate(requireContext(), R.layout.alert_dialog_confirm_approve, null)
            views.findViewById<TextView>(R.id.tv_text).text = "Do you want to unban the user?"
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(views)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            views.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            views.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                unbanUser()
                setUserInfo()
                dialog.dismiss()
            }
        }

    }

    private fun banUser() {
        userInfo.ban = true
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val updateUser = DatabaseRepo().updateUserData(args.userID, userInfo)

                requireActivity().runOnUiThread {
                    if (updateUser) {
                        val views =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        views.findViewById<TextView>(R.id.tv_text).text = "Successfully ban user"
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setView(views)
                        val dialog = builder.create()
                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    } else {
                        val views =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        views.findViewById<TextView>(R.id.tv_text).text = "Unsuccessful ban user"
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setView(views)
                        val dialog = builder.create()
                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
            }
        }
    }

    private fun unbanUser() {
        userInfo.ban = false
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Default) {
                val updateUser = DatabaseRepo().updateUserData(args.userID, userInfo)

                requireActivity().runOnUiThread {
                    if (updateUser) {
                        val views =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        views.findViewById<TextView>(R.id.tv_text).text = "Successfully unban user"
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setView(views)
                        val dialog = builder.create()
                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    } else {
                        val views =
                            View.inflate(
                                requireContext(),
                                R.layout.alert_dialog_complete_request,
                                null
                            )
                        views.findViewById<TextView>(R.id.tv_text).text = "Unsuccessful unban user"
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setView(views)
                        val dialog = builder.create()
                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        views.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()

                    }
                }
            }
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