package com.hidil.fypsmartfoodbank.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.StorageItemListLayoutBinding
import com.hidil.fypsmartfoodbank.model.FoodBank
import com.hidil.fypsmartfoodbank.model.StorageCompact
import com.hidil.fypsmartfoodbank.ui.fragments.FoodBankInfoFragment
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class AvailableFoodBankItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<StorageCompact>,
    private val fragment: FoodBankInfoFragment
): RecyclerView.Adapter<AvailableFoodBankItemsListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: StorageItemListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(StorageItemListLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
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
            holder.binding.tvItemQuantity.text = "QTY - ${model.itemQuantity}/${model.maximumCapacity}"
            holder.binding.tvAmount.text = itemAmount[position].toString()

            holder.binding.ivAdd.setOnClickListener {
                if (itemAmount[position] <= model.itemQuantity-1){
                    itemAmount[position]++
                    totalItem++
                    holder.binding.tvAmount.text = itemAmount[position].toString()
                }
            }

            holder.binding.ivRemove.setOnClickListener {
                if (itemAmount[position] >= 1) {
                    itemAmount[position]--
                    totalItem--
                    holder.binding.tvAmount.text = itemAmount[position].toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}