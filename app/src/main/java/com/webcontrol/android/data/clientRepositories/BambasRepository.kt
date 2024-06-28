package com.webcontrol.android.data.clientRepositories

import com.webcontrol.android.data.RestInterfaceBambas
import com.webcontrol.android.data.model.DocumentsBambas
import com.webcontrol.android.data.model.WorkerBambas
import com.webcontrol.android.data.network.ApiResponseBambas
import com.webcontrol.android.data.network.ApiResponseBambasCredential
import com.webcontrol.android.data.network.ApiResponseSearchBambas
import com.webcontrol.android.data.network.TokenBambasRequest



interface BambasRepository {
    suspend fun getToken(request: TokenBambasRequest): ApiResponseBambas
    suspend fun getCredentials(rut:String): ApiResponseBambasCredential<WorkerBambas>
    suspend fun getDocumentos(rut:String,pase:Int):ApiResponseBambasCredential<List<DocumentsBambas>>
    suspend fun getSearchWorker(rut:String): List<ApiResponseSearchBambas>
}

class BambasRepositoryImpl(
    val api: RestInterfaceBambas
): BambasRepository {
    override suspend fun getToken(request: TokenBambasRequest) = api.getToken(request)
    override suspend fun getCredentials(rut: String) =api.getWorkerCredencial(rut)
    override suspend fun getDocumentos(rut: String,pase:Int) =api.getDocumentos(rut,pase)
    override suspend fun getSearchWorker(rut: String) =api.getWorkerSearchCredencial(rut)



}