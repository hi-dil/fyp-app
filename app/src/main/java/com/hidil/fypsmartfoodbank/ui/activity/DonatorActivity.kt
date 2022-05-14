package com.hidil.fypsmartfoodbank.ui.activity

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.hidil.fypsmartfoodbank.R
import com.hidil.fypsmartfoodbank.databinding.ActivityDonatorBinding

class DonatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonatorBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonatorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_donator_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardDonatorFragment,
                R.id.locationFragment2,
                R.id.donationRequestFragment,
                R.id.userProfileFragment2
            )
        )

        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavigationView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
        binding.navView.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
        binding.navView.visibility = View.VISIBLE
    }
}