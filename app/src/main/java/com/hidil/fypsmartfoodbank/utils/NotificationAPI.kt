package com.hidil.fypsmartfoodbank.utils

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers(
        "Authorization: key=${Constants.FCM_SERVER_KEY}",
        "Content-Type:${Constants.FCM_CONTENT_TYPE}"
    )
    @POST("fcm/send")
    suspend fun PostNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}