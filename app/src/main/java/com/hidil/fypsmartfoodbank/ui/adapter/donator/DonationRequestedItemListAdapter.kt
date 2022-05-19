package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.hidil.fypsmartfoodbank.databinding.ListLayoutDonationItemBinding
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DonationRequestedItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemListDonation>,
    private val fragment: Fragment
): RecyclerView.Adapter<DonationRequestedItemListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutDonationItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(ListLayoutDonationItemBinding.inflate(LayoutInflater.from(context), parent, false))
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

//        holder.binding.etExpiryDate.setOnClickListener {
//            // Initiation date picker with
//            // MaterialDatePicker.Builder.datePicker()
//            // and building it using build()
//            val datePicker = MaterialDatePicker.Builder.datePicker().build()
//            datePicker.show(fragment.requireActivity().supportFragmentManager, "DatePicker")
//
//            // Setting up the event for when ok is clicked
//            datePicker.addOnPositiveButtonClickListener {
//                // formatting date in dd-mm-yyyy format.
//                val dateFormatter = SimpleDateFormat("dd MMMM yyyy")
//                val date = dateFormatter.format(Date(it))
//                holder.binding.etExpiryDate.setText(date)
//            }
//
//            // Setting up the event for when cancelled is clicked
//            datePicker.addOnNegativeButtonClickListener {
//                Toast.makeText(context, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
//            }
//
//            // Setting up the event for when back button is pressed
//            datePicker.addOnCancelListener {
//                Toast.makeText(context, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
//            }
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}