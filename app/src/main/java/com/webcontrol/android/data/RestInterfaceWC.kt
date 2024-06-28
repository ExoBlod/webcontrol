package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.Usuarios
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.db.entity.WorkerUnregistered
import com.webcontrol.android.data.model.Device
import com.webcontrol.android.data.db.entity.Message
import com.webcontrol.android.data.db.entity.PuntoMarcacion
import com.webcontrol.android.data.model.WorkerLocation
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.dto.ChangeDataDto
import com.webcontrol.android.data.network.dto.SyncEstadoDto
import com.webcontrol.android.ui.login.dto.LoginDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RestInterfaceWC {
    @POST("login")
    fun login(@Body loginDto: LoginDto?): Call<ApiResponse<List<Usuarios>>>

    @POST("user/register")
    fun insertDevice(@Body device: Device?): Call<ApiResponse<Any>>

    @POST("user")
    fun signUp(@Body worker: WorkerUnregistered?): Call<ApiResponse<List<Worker>>>

    @POST("user/sendsms")
    fun sendSMS(@Body worker: WorkerUnregistered?): Call<ApiResponse<List<Worker>>>

    @POST("user/validarsms")
    fun validarSMS(@Body worker: WorkerUnregistered?): Call<ApiResponse<List<Worker>>>

    @POST("user/changepass")
    fun changePassword(@Body device: Worker?): Call<ApiResponse<Any>>

    @GET("messages/{rut}/{idSync}")
    fun getMessages(@Path("rut") rut: String?, @Path("idSync") idSync: Long): Call<ApiResponse<List<Message>>>

    @POST("messages/read")
    fun messageRead(@Body message: Message?): Call<ApiResponse<Any>>

    @POST("messages/important")
    fun messageImportant(@Body message: Message?): Call<ApiResponse<Any>>

    @POST("messages/deleted")
    fun messageDeleted(@Body message: Message?): Call<ApiResponse<Any>>

    @POST("user/requestchangepass")
    fun requestChangePasswordVerify(@Body worker: Worker?): Call<ApiResponse<Any>>

    @POST("user/changepassverify")
    fun changePasswordVerify(@Body worker: Worker?): Call<ApiResponse<Any>>

    @POST("messages/syncStatus")
    fun syncStatus(@Body status: List<SyncEstadoDto?>?): Call<ApiResponse<List<SyncEstadoDto>>>

    @POST("messages/syncImportant")
    fun syncImportantes(@Body importants: List<SyncEstadoDto?>?): Call<ApiResponse<List<SyncEstadoDto>>>

    @POST("user/verifychangedata")
    fun verifyChangeData(@Body data: ChangeDataDto?): Call<ApiResponse<Any>>

    @POST("user/changedata")
    fun changeData(@Body data: ChangeDataDto?): Call<ApiResponse<Any>>

    @get:POST("geofences")
    val geofences: Call<ApiResponse<List<PuntoMarcacion>>>

    @GET("datetime")
    fun datetime(): Call<ApiResponseAnglo<String>>

    @POST("location/insert")
    fun sendWorkerLocation(@Body data: WorkerLocation): Call<ApiResponseAnglo<Any>>

    @POST("user/{rut}/delete")
    fun accountDeleted(@Path("rut") rut: String?): Call<ApiResponse<Any>>
}