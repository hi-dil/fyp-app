package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.RequestListLayoutBinding
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class FavouriteFoodBankListAdapter(
    private val context: Context,
    private var list: ArrayList<FavouriteFoodBank>,
    private val fragment: Fragment
) : RecyclerView.Adapter<FavouriteFoodBankListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: RequestListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RequestListLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.foodBankImage, holder.binding.ivFoodBankImage)
        holder.binding.tvFbName.text = model.foodBankName
        holder.binding.tvAddress.text = model.address
        holder.binding.tvProgress.visibility = View.GONE

        holder.binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${model.lat},${model.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            fragment.requireActivity().startActivity(mapIntent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}