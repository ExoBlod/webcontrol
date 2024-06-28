package com.webcontrol.android.data

import com.webcontrol.android.data.model.DocumentLaPoderosa
import com.webcontrol.android.data.model.WorkerLaPoderosa
import com.webcontrol.android.data.network.ApiResponseLaPoderosaCredencial
import com.webcontrol.android.data.network.ApiResponseTokenLaPoderosa
import com.webcontrol.android.data.network.TokenPoderosaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestInterfaceLaPoderosa {
    @POST("auth")
    suspend fun getToken(@Body request: TokenPoderosaRequest): ApiResponseTokenLaPoderosa

    @GET("worker/credentialvirtual")
    suspend fun getWorkerCredencial(@Query("workerId") worker: String): ApiResponseLaPoderosaCredencial<WorkerLaPoderosa>

    @GET("worker/Documentos")
    suspend fun getDocumentos(
        @Query("workerId") worker: String,
        @Query("paseId") paseId: Int
    ): ApiResponseLaPoderosaCredencial<List<DocumentLaPoderosa>>
}