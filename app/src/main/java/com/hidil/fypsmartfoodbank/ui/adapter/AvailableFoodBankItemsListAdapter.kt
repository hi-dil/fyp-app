package com.hidil.fypsmartfoodbank.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutStorageItemBinding
import com.hidil.fypsmartfoodbank.model.StorageCompact
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.FoodBankInfoFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.FoodBankInfoFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class AvailableFoodBankItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<StorageCompact>,
    private val fragment: FoodBankInfoFragment
) : RecyclerView.Adapter<AvailableFoodBankItemsListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutStorageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutStorageItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    private var itemAmount: ArrayList<Int> = ArrayList()
    private val itemQuantity: Int = 0
    private var totalItem: Int = 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        itemAmount.add(itemQuantity)
        if (model.itemQuantity > 0) {
            GlideLoader(context).loadStoragePicture(model.itemImage, holder.binding.ivItemImage)
            holder.binding.tvItemName.text = model.item
            holder.binding.tvStorageID.text = model.storageName
            holder.binding.tvItemQuantity.text =
                "QTY - ${model.itemQuantity}/${model.maximumCapacity}"
            holder.binding.tvAmount.text = itemAmount[position].toString()

            for (i in itemAmount) {
                totalItem += i
            }

            holder.binding.ivAdd.setOnClickListener {
                if (itemAmount[position] <= model.itemQuantity - 1 && totalItem <= 19) {
                    itemAmount[position]++
                    totalItem++
                    holder.binding.tvAmount.text = itemAmount[position].toString()
                    fragment.changeItemAmount(position, true)
                } else {
                    Toast.makeText(
                        context,
                        "Cannot add more than the current stock",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            holder.binding.ivRemove.setOnClickListener {
                if (itemAmount[position] >= 1) {
                    itemAmount[position]--
                    totalItem--
                    holder.binding.tvAmount.text = itemAmount[position].toString()
                    fragment.changeItemAmount(position, false)
                } else {
                    Toast.makeText(context, "The items must not be less than 0", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            holder.binding.ivItemImage.setOnClickListener { view ->
                view.findNavController().navigate(
                    FoodBankInfoFragmentDirections.actionFoodBankInfoFragmentToStorageInfoFragment(
                        model.id
                    )
                )
            }

            if (fragment.requireActivity() is BeneficiaryMainActivity) {
                (fragment.activity as BeneficiaryMainActivity?)?.hideBottomNavigationView()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}