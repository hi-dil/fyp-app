package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutImageBinding
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class ShowImageLinkAdapter(
    private val context: Context,
    private val list: ArrayList<String>
): RecyclerView.Adapter<ShowImageLinkAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutImageBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadFoodBankPicture(model, holder.binding.ivImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}