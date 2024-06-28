package com.webcontrol.pucobre.data

import com.webcontrol.pucobre.data.dto.TokenRequest
import com.webcontrol.pucobre.data.model.ApiResponse
import com.webcontrol.pucobre.data.model.WorkerCredential
import com.webcontrol.pucobre.data.model.WorkerPucobre
import retrofit2.http.*

interface PucobreApiService {

    @POST("v1/worker/authenticate")
    suspend fun getToken(@Body tokenRequest: TokenRequest): ApiResponse<String>

    @GET("v3/credentialCertificado/{rut}")
    suspend fun getCredential(@Path("rut") rut: String): ApiResponse<WorkerCredential?>

    @POST("worker")
    suspend fun getWorker(@Body params: WorkerPucobre): ApiResponse<WorkerPucobre?>
}