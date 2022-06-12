package com.hidil.fypsmartfoodbank.repository

import com.hidil.fypsmartfoodbank.utils.Constants
import com.hidil.fypsmartfoodbank.utils.NotificationAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.FCM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}