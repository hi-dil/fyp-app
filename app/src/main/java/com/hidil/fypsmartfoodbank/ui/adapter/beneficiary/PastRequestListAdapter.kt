package com.hidil.fypsmartfoodbank.ui.adapter.beneficiary

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.RequestListLayoutBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.ui.activity.BeneficiaryMainActivity
import com.hidil.fypsmartfoodbank.ui.fragments.beneficiary.ClaimRequestFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader

open class PastRequestListAdapter(
    private val context: Context,
    private var list: ArrayList<Request>,
    private val fragment: Fragment
): RecyclerView.Adapter<PastRequestListAdapter.MyViewHolder>()  {

    class MyViewHolder(val binding: RequestListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RequestListLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadFoodBankPicture(model.foodBankImage, holder.itemView.findViewById(R.id.iv_foodBankImage))
        holder.binding.tvFbName.text = model.foodBankName
        holder.binding.tvAddress.text = model.address
        holder.binding.tvProgress.text = "Completed"
        holder.binding.tvProgress.background = ContextCompat.getDrawable(context, R.drawable.complete_tag)
        holder.binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.white))

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(ClaimRequestFragmentDirections.actionClaimRequestFragmentToClaimRequestDetailsFragment(model))

            if(fragment.requireActivity() is BeneficiaryMainActivity) {
                (fragment.activity as BeneficiaryMainActivity?)?.hideBottomNavigationView()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}