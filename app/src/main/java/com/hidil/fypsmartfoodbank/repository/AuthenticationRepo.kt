package com.hidil.fypsmartfoodbank.repository

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hidil.fypsmartfoodbank.model.User
import com.hidil.fypsmartfoodbank.ui.activity.*
import com.hidil.fypsmartfoodbank.ui.fragments.UserProfileFragment

class AuthenticationRepo {

    private val mAuth = FirebaseAuth.getInstance()

    fun createUser(activity: SignUp, email: String, password: String, user: User) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity.hideProgressDialog()
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    activity.showErrorSnackBar(
                        "your are registered successfully. Your user id is ${firebaseUser.uid}",
                        false
                    )

                    val register = User(
                        firebaseUser.uid,
                        user.userRole,
                        user.name,
                        user.email,
                        user.image,
                        user.monthlyIncome,
                        user.mobileNumber,
                        user.state,
                        user.city
                    )

                    DatabaseRepo().registerUser(activity, register)

//                    mAuth.signOut()
//                    activity.finish()
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userLogin(activity: Login, email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    DatabaseRepo().getUserDetails(activity)
                } else {
                    activity.hideProgressDialog()
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

    fun getCurrentUserID(): String {
        val currentUser = mAuth.currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun logout(fragment: UserProfileFragment) {
        mAuth.signOut()
        fragment.activity?.finish()
        fragment.startActivity(Intent(fragment.activity, Login::class.java))
    }
}