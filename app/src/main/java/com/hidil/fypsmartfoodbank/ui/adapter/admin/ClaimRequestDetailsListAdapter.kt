package com.hidil.fypsmartfoodbank.ui.adapter.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutItemBinding
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class ClaimRequestDetailsListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemList>
) : RecyclerView.Adapter<ClaimRequestDetailsListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadFoodBankPicture(model.itemImage, holder.binding.ivItemImage)
        holder.binding.tvItemName.text = model.itemName
        holder.binding.tvStorageID.text = model.storageName
        holder.binding.tvAmount.text = model.itemQuantity.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}