package com.webcontrol.android.data

import com.webcontrol.android.data.model.sgscm.AuthenticateRequest
import com.webcontrol.android.data.model.sgscm.CredencialSgscmResponse
import com.webcontrol.android.data.model.sgscm.WorkerSgscmResponse
import com.webcontrol.android.data.network.ApiResponseAnglo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestInterfaceSgscm {
    @GET("worker")
    fun getWorkerSgscm(@Query("rut") rut: String): Call<ApiResponseAnglo<WorkerSgscmResponse?>>

    @POST("login/worker/authenticate")
    fun getTokenSgscm(@Body authenticateRequest: AuthenticateRequest): Call<ApiResponseAnglo<String>>

    @GET("worker/credencial")
    fun getWorkerCredencialSgscm(@Query("rut") rut: String): Call<ApiResponseAnglo<CredencialSgscmResponse?>>

}