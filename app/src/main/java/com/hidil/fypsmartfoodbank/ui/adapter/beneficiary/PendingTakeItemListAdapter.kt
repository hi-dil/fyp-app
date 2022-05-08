package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ItemListLayoutBinding
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestDetailsFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class PendingTakeItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemList>,
    private val request: Request
) : RecyclerView.Adapter<PendingTakeItemListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemListLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.itemImage, holder.binding.ivItemImage)
        holder.binding.tvItemName.text = model.itemName
        holder.binding.tvStorageID.text = "FoodBank StorageCompact ID - ${model.storageName}"
        holder.binding.tvAmount.text = model.itemQuantity.toString()
        holder.binding.ivItemStatus.visibility = View.VISIBLE
        holder.itemView.setOnClickListener { view ->

            view.findNavController().navigate(
            ClaimRequestDetailsFragmentDirections.actionClaimRequestDetailsFragmentToUnlockOptionsFragment(request, position, model))
        }

        if (model.completed) {
            holder.binding.ivItemStatus.setImageResource(R.drawable.ic_complete)
            holder.itemView.setOnClickListener {
                Toast.makeText(context, "You already claimed this item", Toast.LENGTH_SHORT).show()
            }
        } else {

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}