package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ListLayoutRequestBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragment
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.DashboardFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class ActiveRequestListAdapter(
    private val context: Context,
    private var list: ArrayList<Request>,
    private val fragment: Fragment
) : RecyclerView.Adapter<ActiveRequestListAdapter.MyViewHolder>() {

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
//        holder.binding.tvAddress.text = model.address


        val dateFormat = "dd MMMM yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = model.lastUpdate
        val date = formatter.format(calendar.time)

        holder.binding.tvAddress.text = "Last update - $date"

        holder.itemView.setOnClickListener { view ->
            when (fragment) {
                is DashboardFragment -> {
                    view.findNavController().navigate(
                        DashboardFragmentDirections.actionDashboardFragmentToClaimRequestDetailsFragment(
                            model
                        )
                    )

                    if (fragment.requireActivity() is BeneficiaryMainActivity) {
                        (fragment.activity as BeneficiaryMainActivity?)?.hideBottomNavigationView()
                    }
                }

                is ClaimRequestFragment -> {
                    view.findNavController().navigate(
                        ClaimRequestFragmentDirections.actionClaimRequestFragmentToClaimRequestDetailsFragment(
                            model
                        )
                    )

                    if (fragment.requireActivity() is BeneficiaryMainActivity) {
                        (fragment.activity as BeneficiaryMainActivity?)?.hideBottomNavigationView()
                    }
                }
            }
        }

        holder.binding.ivNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${model.lat},${model.long}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            fragment.requireActivity().startActivity(mapIntent)
        }

        if (model.approved) {
            holder.binding.tvProgress.text = "Approved"
            holder.binding.tvProgress.background = ContextCompat.getDrawable(fragment.requireContext(), R.drawable.approved_tag)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}