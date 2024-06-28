package com.webcontrol.android.data.clientRepositories

import com.webcontrol.android.data.RestInterfaceLaPoderosa
import com.webcontrol.android.data.model.DocumentLaPoderosa
import com.webcontrol.android.data.model.WorkerLaPoderosa
import com.webcontrol.android.data.network.ApiResponseLaPoderosaCredencial
import com.webcontrol.android.data.network.ApiResponseTokenLaPoderosa
import com.webcontrol.android.data.network.TokenPoderosaRequest

interface LaPoderosaRepository {
    suspend fun getToken(request: TokenPoderosaRequest): ApiResponseTokenLaPoderosa
    suspend fun getCredentials(rut: String): ApiResponseLaPoderosaCredencial<WorkerLaPoderosa>
    suspend fun getDocumentos(
        rut: String,
        pase: Int
    ): ApiResponseLaPoderosaCredencial<List<DocumentLaPoderosa>>
}

class LaPoderosaRepositoryImpl(val api: RestInterfaceLaPoderosa) :
    LaPoderosaRepository {
    override suspend fun getToken(request: TokenPoderosaRequest): ApiResponseTokenLaPoderosa =
        api.getToken(request)

    override suspend fun getCredentials(rut: String): ApiResponseLaPoderosaCredencial<WorkerLaPoderosa> =
        api.getWorkerCredencial(rut)

    override suspend fun getDocumentos(
        rut: String,
        pase: Int
    ): ApiResponseLaPoderosaCredencial<List<DocumentLaPoderosa>> = api.getDocumentos(rut, pase)

}
