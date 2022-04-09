package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class FavouriteFoodBankListAdapter(
    private val context: Context,
    private var list: ArrayList<FavouriteFoodBank>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.active_request_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadFoodBankPicture(
                model.foodBankImage,
                holder.itemView.findViewById(R.id.iv_foodBankImage)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_fbName).text =
                model.foodBankName
            holder.itemView.findViewById<TextView>(R.id.tv_address).text =
                model.address
            holder.itemView.findViewById<TextView>(R.id.tv_progress).visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}