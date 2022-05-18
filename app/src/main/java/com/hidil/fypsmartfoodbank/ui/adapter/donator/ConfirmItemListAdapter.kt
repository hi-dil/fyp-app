package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.hidil.fypsmartfoodbank.databinding.ListLayoutDonationItemBinding
import com.hidil.fypsmartfoodbank.model.ItemList
import com.hidil.fypsmartfoodbank.model.ItemListDonation
import com.hidil.fypsmartfoodbank.ui.fragments.donator.ConfirmDonationRequestFragment
import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConfirmItemListAdapter(
    private val context: Context,
    private val list: ArrayList<ItemList>,
    private val fragment: ConfirmDonationRequestFragment
) : RecyclerView.Adapter<ConfirmItemListAdapter.MyViewHolder>() {
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
        var date: Long = 0

        holder.binding.etExpiryDate.setOnClickListener {
            // Initiation date picker with
            // MaterialDatePicker.Builder.datePicker()
            // and building it using build()
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(fragment.requireActivity().supportFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                // formatting date in dd-mm-yyyy format.
                date = it
                val dateFormatter = SimpleDateFormat("dd MMMM yyyy")
                val date = dateFormatter.format(Date(it))
                holder.binding.etExpiryDate.setText(date)
            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(context, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG)
                    .show()
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                Toast.makeText(context, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }

        holder.binding.etExpiryDate.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val itemList = ItemListDonation(
                    model.completed,
                    model.itemName,
                    model.itemQuantity,
                    model.itemImage,
                    model.storageID,
                    model.storageName,
                    model.storagePIN,
                    date,
                    holder.binding.etBrand.text.toString()
                )
                fragment.updateItemList(itemList, holder.adapterPosition)
            }
        })

        holder.binding.etBrand.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val itemList = ItemListDonation(
                    model.completed,
                    model.itemName,
                    model.itemQuantity,
                    model.itemImage,
                    model.storageID,
                    model.storageName,
                    model.storagePIN,
                    date,
                    holder.binding.etBrand.text.toString()
                )
                fragment.updateItemList(itemList, holder.adapterPosition)
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}