package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

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
import com.hidil.fypsmartfoodbank.model.FavouriteFoodBank
import com.hidil.fypsmartfoodbank.repository.DatabaseRepo
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class FavouriteFoodBankListAdapter(
    private val context: Context,
    private var list: ArrayList<FavouriteFoodBank>,
    private val fragment: Fragment,
    private val currentLat: Double,
    private val currentLong: Double
) : RecyclerView.Adapter<FavouriteFoodBankListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        GlideLoader(context).loadFoodBankPicture(
            model.foodBankImage,
            holder.binding.ivFoodBankImage
        )
        holder.binding.tvFbName.text = model.foodBankName
        holder.binding.tvAddress.text = model.address
        holder.binding.tvProgress.visibility = View.GONE
        holder.binding.tvDistance.visibility = View.VISIBLE

        val distance =
            distanceInKm(currentLat, currentLong, model.lat.toDouble(), model.long.toDouble())
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
            CoroutineScope(IO).launch {
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

    private fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }


}