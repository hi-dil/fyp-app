package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ListLayoutDonationItemBinding
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class DonationRequestedItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemListDonation>,
    private val fragment: Fragment
) : RecyclerView.Adapter<DonationRequestedItemListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutDonationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutDonationItemBinding.inflate(
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
        holder.binding.tvStorageID.text = "FoodBank StorageCompact ID - ${model.storageName}"
        holder.binding.tvAmount.text = model.itemQuantity.toString()
        holder.binding.etBrand.setText(model.itemBrand)

        val dateFormat = "dd MMMM yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = model.expiryDate
        val date = formatter.format(calendar.time)

        holder.binding.etExpiryDate.setText(date)

        holder.binding.etBrand.isEnabled = false
        holder.binding.etExpiryDate.isEnabled = false

        // check if expiry date is less than 6 months
        val currentMillis = System.currentTimeMillis()
        val timeLeftBeforeExpire = model.expiryDate - currentMillis

        if (timeLeftBeforeExpire <= 7889400000) {
            holder.binding.llWarning.visibility = View.VISIBLE
            holder.binding.ivWarningCircle.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.danger
                )
            )
            holder.binding.tvWarningText.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.danger
                )
            )
            holder.binding.tvWarningText.text =
                "Warning: The donated items has left than 3 months of shelf life left"
        } else if (timeLeftBeforeExpire <= 15778800000) {
            holder.binding.llWarning.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}