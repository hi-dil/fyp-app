package com.hidil.fypsmartfoodbank.ui.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hidil.fypsmartfoodbank.R
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private lateinit var mProgressDialog: Dialog

// display custom snackbar view
fun Activity.showErrorSnackBar(message: String, errorMessage: Boolean) {
    val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
    snackBar.view

    if (errorMessage) {
        snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.danger))
    } else {
        snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.primaryColor))
    }
    snackBar.show()
}

fun Activity.showProgressDialog() {
    mProgressDialog = Dialog(this)
    mProgressDialog.setContentView(R.layout.alert_dialog)
    mProgressDialog.setCancelable(false)
    mProgressDialog.setCanceledOnTouchOutside(false)
    mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mProgressDialog.show()
}

fun hideProgressDialog() {
    mProgressDialog.dismiss()
}

// calculate the distance between two locations and return in km
fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
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

