package com.azrinurvani.mobile.firebasechatapp.network

import com.azrinurvani.mobile.firebasechatapp.constants.Constants
import com.azrinurvani.mobile.firebasechatapp.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}