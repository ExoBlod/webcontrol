package com.webcontrol.android.data

import com.webcontrol.android.data.model.Capacitaciones
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.model.WorkerEA
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.BarrickResponse
import com.webcontrol.android.data.network.CourseRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestInterfaceEA {
    @GET("worker?")
    fun getWorker(@Query("rut") rut: String): Call<ApiResponseAnglo<WorkerEA?>>

    @POST("auth")
    fun getToken(@Body workerEA: WorkerEA): Call<ApiResponseAnglo<String>>

    @GET("worker/credencial?")
    fun getWorkerCredencial(@Query("rut") rut: String): Call<ApiResponseAnglo<WorkerEA>>

    @GET("course/courses")
    suspend fun getCourses(@Query("rut") rut: String, @Query("division") div: String): BarrickResponse<List<Capacitaciones>>

    @GET("worker/divisions")
    suspend fun getDivisionList(): BarrickResponse<List<Division>>
}