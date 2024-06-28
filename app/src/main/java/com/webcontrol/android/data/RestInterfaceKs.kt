package com.webcontrol.android.data

import com.webcontrol.android.data.model.WorkerKs
import com.webcontrol.android.data.model.WorkerSearchResultKs
import com.webcontrol.android.data.network.ApiResponseKs
import com.webcontrol.android.data.network.TokenKsRequest
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceKs {
    @POST("auth")
    fun getToken(@Body request: TokenKsRequest): Call<ApiResponseKs>

    @GET("worker")
    fun getWorker(@Query("workerId") workerId: String): Call<WorkerSearchResultKs>

    @GET("worker/credential")
    fun getWorkerCredencial(@Query("workerId") workerId: String): Call<WorkerKs>
}