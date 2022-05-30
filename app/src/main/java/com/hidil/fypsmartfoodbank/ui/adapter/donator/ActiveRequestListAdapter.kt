package com.hidil.fypsmartfoodbank.ui.adapter.donator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ListLayoutRequestBinding
import com.hidil.fypsmartfoodbank.model.DonationRequest
import com.hidil.fypsmartfoodbank.ui.activity.DonatorActivity
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DashboardDonatorFragment
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DashboardDonatorFragmentDirections
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DonationRequestFragment
import com.hidil.fypsmartfoodbank.ui.fragments.donator.DonationRequestFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader

class ActiveRequestListAdapter(
    private val context: Context,
    private var list: ArrayList<DonationRequest>,
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

        holder.itemView.setOnClickListener { view ->
            when (fragment) {
                is DonationRequestFragment -> {
                    view.findNavController().navigate(
                        DonationRequestFragmentDirections.actionDonationRequestFragmentToDonationRequestDetailsFragment(
                            model
                        )
                    )
                }
                is DashboardDonatorFragment -> {
                    view.findNavController().navigate(
                        DashboardDonatorFragmentDirections.actionDashboardDonatorFragmentToDonationRequestDetailsFragment(
                            model
                        )
                    )
                }
            }
            if (fragment.requireActivity() is DonatorActivity) {
                (fragment.activity as DonatorActivity?)?.hideBottomNavigationView()
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
            holder.binding.tvProgress.background =
                ContextCompat.getDrawable(fragment.requireContext(), R.drawable.approved_tag)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}