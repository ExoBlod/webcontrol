package com.webcontrol.android

import com.webcontrol.android.data.model.Company
import com.webcontrol.android.data.network.ApiResponseAnglo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FirebaseMessageInterface {

    @GET("api/datetime")
    fun datetime(): Call<ApiResponseAnglo<String>>

}