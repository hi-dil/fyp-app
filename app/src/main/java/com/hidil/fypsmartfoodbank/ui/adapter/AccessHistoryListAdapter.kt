package com.hidil.fypsmartfoodbank.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutUserAccessBinding
import com.hidil.fypsmartfoodbank.model.AccessHistory
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.StorageInfoFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AccessHistoryListAdapter(
    private val context: Context,
    private val list: ArrayList<AccessHistory>,
    private val fragment: StorageInfoFragment
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
        holder.binding.tvAmountTook.text = "Took ${model.amountTook} items"
        val date = converDateFromMillis(model.lastVisited)
        holder.binding.tvDate.text = date

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(
                StorageInfoFragmentDirections.actionStorageInfoFragmentToUserInfoFragment(model.userID)
            )
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