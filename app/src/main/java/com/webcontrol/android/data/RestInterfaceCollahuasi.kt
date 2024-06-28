package com.webcontrol.android.data

import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.LocalRequest
import com.webcontrol.android.data.network.VehicleRequest
import com.webcontrol.android.data.network.WorkerRequest
import retrofit2.http.*

interface RestInterfaceCollahuasi {
    @GET("divisions")
    suspend fun getDivisions(): ApiResponseAnglo<List<Division>>

    @POST("locals")
    suspend fun getLocals(@Body request: LocalRequest): ApiResponseAnglo<List<Local>>

    @POST("vehicle")
    suspend fun getVehicle(@Body request: VehicleRequest): ApiResponseAnglo<Vehiculo?>

    @POST("worker")
    suspend fun getWorker(@Body request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>

    @POST("preaccess")
    suspend fun sendControlList(@Body list: List<RegControl>): ApiResponseAnglo<Any>

    @POST("worker/authenticate")
    suspend fun login(@Body request: WorkerRequest): ApiResponseAnglo<String?>

}