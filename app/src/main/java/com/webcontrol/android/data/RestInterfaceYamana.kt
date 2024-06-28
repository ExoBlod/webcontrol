package com.webcontrol.android.data

import com.webcontrol.android.data.model.WorkerYamana
import com.webcontrol.android.data.network.ApiResponseAnglo
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceYamana {
    @GET("worker/{rut}")
    fun getWorker(@Path("rut") rut: String): Call<ApiResponseAnglo<WorkerYamana?>>

    @POST("auth")
    fun getToken(@Body workerKs: WorkerYamana): Call<ApiResponseAnglo<String>>

    @GET("workerCertificado/{rut}")
    fun getWorkerCredencial(@Path("rut") rut: String): Call<ApiResponseAnglo<WorkerYamana>>
}