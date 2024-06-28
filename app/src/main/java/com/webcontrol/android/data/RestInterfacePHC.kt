package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RestInterfacePHC {
    @GET("worker/getWorker")
    fun getWorker(@Query("workerId") workerId: String): Call<ApiResponsePHC<WorkerPHC>>

    @POST("auth")
    suspend fun auth(@Body request: TokenPHCRequest): ApiResponseTokenPHC

    @GET("worker/credential/qr")
    suspend fun getCredentials(@Query("workerId") workerId: String): ApiResponsePHC<WorkerCredentialPHC>

    @GET("worker/credential")
    fun getWorkerCredencial(@Query("workerId") workerId: String): Call<WorkerCredentialPHC>
}