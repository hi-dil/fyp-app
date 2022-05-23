package com.hidil.fypsmartfoodbank.ui.adapter.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hidil.fypsmartfoodbank.databinding.ListLayoutRequestVerificationBinding
import com.hidil.fypsmartfoodbank.model.Request
import com.hidil.fypsmartfoodbank.ui.activity.AdminMainActivity
import com.hidil.fypsmartfoodbank.ui.fragments.admin.DashboardAdminFragment
import com.hidil.fypsmartfoodbank.ui.fragments.admin.DashboardAdminFragmentDirections
import com.hidil.fypsmartfoodbank.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class RequestListAdapter(
    private val context: Context,
    private var list: ArrayList<Request>,
    private val fragment: Fragment
) : RecyclerView.Adapter<RequestListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ListLayoutRequestVerificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListLayoutRequestVerificationBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadUserPicture(
            model.userImage, holder.binding.ivUserImage
        )

        holder.binding.tvUserName.text = model.userName
        holder.binding.tvFbName.text = model.foodBankName

        val dateFormat = "dd MMMM yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = model.requestDate
        val date = formatter.format(calendar.time)

        holder.binding.tvRequestDate.text = "Request Date - $date"

        var totalItems = 0
        for (i in model.items) {
            totalItems += i.itemQuantity
        }

        holder.binding.tvItemAmount.text = "Total request item - $totalItems items"

        holder.itemView.setOnClickListener { view ->
            when (fragment) {
                is DashboardAdminFragment -> view.findNavController().navigate(
                    DashboardAdminFragmentDirections.actionDashboardAdminFragmentToDetailVerificationClaimFragment(
                        model
                    )
                )
            }
            if (fragment.requireActivity() is AdminMainActivity) {
                (fragment.requireActivity() as AdminMainActivity).hideBottomNavigationView()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}