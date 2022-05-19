package com.hidil.fypsmartfoodbank.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutRequestBinding
import com.hidil.fypsmartfoodbank.model.Location
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat

class NearbyFoodBankListAdapter(
    private val context: Context,
    private val list: ArrayList<Location>,
    private val fragment: Fragment
): RecyclerView.Adapter<NearbyFoodBankListAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ListLayoutRequestBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutRequestBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.foodBankImage, holder.binding.ivFoodBankImage)
        holder.binding.tvFbName.text = model.foodBankName
        holder.binding.tvAddress.text = model.address
        holder.binding.tvProgress.visibility = View.GONE
        holder.binding.tvDistance.visibility = View.VISIBLE

        val distance = model.distance
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val rfDistance = df.format(distance)
        holder.binding.tvDistance.text = "${rfDistance} km"

        holder.binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${model.lat},${model.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            fragment.requireActivity().startActivity(mapIntent)
        }

        holder.itemView.setOnClickListener { view ->
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Default) {
                    val user = DatabaseRepo().getUserDetailAsync()
                    fragment.requireActivity().runOnUiThread {
                        view.findNavController().navigate(
                            DashboardFragmentDirections.actionDashboardFragmentToFoodBankInfoFragment(
                                user,
                                model.foodBankID,
                                false
                            )
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}