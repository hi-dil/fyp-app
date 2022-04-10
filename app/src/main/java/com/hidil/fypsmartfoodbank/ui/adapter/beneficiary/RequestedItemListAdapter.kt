package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ItemListLayoutBinding
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class RequestedItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemList>
): RecyclerView.Adapter<RequestedItemListAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.itemImage, holder.binding.ivItemImage)
        holder.binding.tvItemName.text = model.itemName
        holder.binding.tvStorageID.text = "FoodBank Storage ID - ${model.storageName}"
        holder.binding.tvAmount.text = model.itemQuantity.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}