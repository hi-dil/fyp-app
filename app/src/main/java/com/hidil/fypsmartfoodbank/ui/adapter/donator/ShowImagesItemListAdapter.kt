package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutImageBinding
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class ShowImagesItemListAdapter(
    private val context: Context,
    private val list: ArrayList<Uri>
) : RecyclerView.Adapter<ShowImagesItemListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutImageBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i("test", list.toString())
        val model = list[position]
        GlideLoader(context).loadUserPicture(model, holder.binding.ivImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}