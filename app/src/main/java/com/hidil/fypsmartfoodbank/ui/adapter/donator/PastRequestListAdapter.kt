package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ListLayoutRequestBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class PastRequestListAdapter(
    private val context: Context,
    private var list: ArrayList<Request>,
    private val fragment: Fragment
): RecyclerView.Adapter<PastRequestListAdapter.MyViewHolder>()  {

    class MyViewHolder(val binding: ListLayoutRequestBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListLayoutRequestBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.foodBankImage, holder.itemView.findViewById(R.id.iv_foodBankImage))
        holder.binding.tvFbName.text = model.foodBankName
        holder.binding.tvAddress.text = model.address
        holder.binding.tvProgress.text = "Completed"
        holder.binding.tvProgress.background = ContextCompat.getDrawable(context, R.drawable.complete_tag)
        holder.binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.white))

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