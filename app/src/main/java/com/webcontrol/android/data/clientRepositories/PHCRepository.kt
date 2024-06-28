package com.webcontrol.android.data.clientRepositories

import com.webcontrol.android.data.RestInterfacePHC
import com.webcontrol.android.data.model.WorkerCredentialPHC
import com.webcontrol.android.data.network.ApiResponsePHC
import com.webcontrol.android.data.network.ApiResponseTokenPHC
import com.webcontrol.android.data.network.TokenPHCRequest

interface PHCRepository {
    suspend fun getToken(request: TokenPHCRequest): ApiResponseTokenPHC

    suspend fun getCredentials(workerId: String): ApiResponsePHC<WorkerCredentialPHC>
}

class PHCRepositoryImpl(val api: RestInterfacePHC) : PHCRepository {
    override suspend fun getToken(request: TokenPHCRequest): ApiResponseTokenPHC = api.auth(request)
    override suspend fun getCredentials(workerId: String): ApiResponsePHC<WorkerCredentialPHC> =
        api.getCredentials(workerId)

}