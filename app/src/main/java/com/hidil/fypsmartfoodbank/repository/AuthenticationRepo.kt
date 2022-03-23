package com.hidil.fypsmartfoodbank.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hidil.fypsmartfoodbank.ui.activity.*

class AuthenticationRepo {

    private val mAuth = FirebaseAuth.getInstance()

    fun createUser(activity: SignUp, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity.hideProgressDialog()
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    activity.showErrorSnackBar(
                        "your are registered successfully. Your user id is ${firebaseUser.uid}",
                        false
                    )

                    mAuth.signOut()
                    activity.finish()
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userLogin(activity: Login, email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                activity.hideProgressDialog()

                if (task.isSuccessful) {
                    activity.showErrorSnackBar("You are logged in successfully", false)
                } else {
                    activity.showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun forgotPassword(activity: ForgotPassword, email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                activity.hideProgressDialog()
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        "Email sent successfully to reset your password",
                        Toast.LENGTH_LONG
                    ).show()
                    activity.finish()
                } else {
                    activity.showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }
}