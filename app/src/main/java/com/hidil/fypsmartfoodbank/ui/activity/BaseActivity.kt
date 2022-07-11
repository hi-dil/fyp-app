package com.hidil.fypsmartfoodbank.ui.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hidil.fypsmartfoodbank.R

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

