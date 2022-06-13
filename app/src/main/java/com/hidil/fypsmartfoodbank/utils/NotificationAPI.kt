package com.hidil.fypsmartfoodbank.utils

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers(
        "Authorization: key=AAAA6GJnW2E:APA91bH6BNPcH9BBxw_2SKzMLmiTPugnTiTQRUVt65dl5sFzyvH-wV3Eq4FqoHMbRuYxnz0hftQFsc3IYtY3auEc_1mwE-NW1VNGbjMJNHWNSuV5B-G52cZeWcQQksPf0GIrF1VL1CSU",
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}