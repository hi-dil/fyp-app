package com.hidil.fypsmartfoodbank.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutUserAccessBinding
import com.hidil.fypsmartfoodbank.model.AccessHistory
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.admin.StorageInfoAdminFragment
import com.hidil.fypsmartfoodbank.ui.fragments.admin.StorageInfoAdminFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class AccessHistoryListAdapter(
    private val context: Context,
    private val list: List<AccessHistory>,
    private val fragment: Fragment
) : RecyclerView.Adapter<AccessHistoryListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutUserAccessBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutUserAccessBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadUserPicture(model.userImage, holder.binding.ivUserImage)
        holder.binding.tvUserName.text = model.userName
        val date = converDateFromMillis(model.lastVisited)
        holder.binding.tvDate.text = date

        when (fragment) {
            is StorageInfoFragment -> {
                if (model.requestType == "claim") {
                    holder.binding.tvUserRole.visibility = View.VISIBLE
                    holder.binding.tvUserRole.text = "Beneficiary"
                    holder.binding.tvAmountTook.text = "Took ${model.amountTook} items"
                }
                holder.itemView.setOnClickListener { view ->
                    view.findNavController().navigate(
                        StorageInfoFragmentDirections.actionStorageInfoFragmentToUserInfoFragment(
                            model.userID
                        )
                    )
                }
            }
            is StorageInfoAdminFragment -> {
                if (model.requestType == "claim") {
                    holder.binding.tvUserRole.visibility = View.VISIBLE
                    holder.binding.tvUserRole.text = "Beneficiary"
                    holder.binding.tvAmountTook.text = "Took ${model.amountTook} items"
                } else {
                    holder.binding.tvUserRole.visibility = View.VISIBLE
                    holder.binding.tvUserRole.text = "Donator"
                    holder.binding.tvAmountTook.text = "Donated ${model.amountTook} items"
                }
                holder.itemView.setOnClickListener { view ->
                    view.findNavController().navigate(
                        StorageInfoAdminFragmentDirections.actionStorageInfoAdminFragmentToUserProfileAdminFragment(
                            model.userID
                        )
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun converDateFromMillis(millis: Long): String {
        val dateFormat = "dd MMMM yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = millis
        return formatter.format(calendar.time)
    }


}