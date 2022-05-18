package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ListLayoutItemBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestDetailsFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DonationRequestDetailsFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class PendingTakeItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemListDonation>,
    private val request: DonationRequest
) : RecyclerView.Adapter<PendingTakeItemListAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ListLayoutItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListLayoutItemBinding.inflate(LayoutInflater.from(context), parent, false))
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
                DonationRequestDetailsFragmentDirections.actionDonationRequestDetailsFragmentToUnlockOptionsDonatorFragment(request, position, model))
        }

        if (model.completed) {
            holder.binding.ivItemStatus.setImageResource(R.drawable.ic_complete)
            holder.itemView.setOnClickListener {
                Toast.makeText(context, "You already claimed this item", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}