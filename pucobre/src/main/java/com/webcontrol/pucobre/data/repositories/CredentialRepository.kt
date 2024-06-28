package com.webcontrol.pucobre.data.repositories

import com.webcontrol.pucobre.data.PucobreApiService
import com.webcontrol.pucobre.data.model.WorkerCredential
import javax.inject.Inject

class CredentialRepository @Inject constructor(private val apiService: PucobreApiService
) : ICredentialRepository {
     override suspend fun getCredential(workerId: String): WorkerCredential? {
        val response = apiService.getCredential(workerId)
        return  response.data
    }
}