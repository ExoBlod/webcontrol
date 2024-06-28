package com.webcontrol.angloamerican.data.repositories

import android.util.Log
import com.webcontrol.angloamerican.data.network.response.AuthorizedAreas
import com.webcontrol.angloamerican.data.network.response.AuthorizedDivisions
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.data.network.response.WorkerCredential
import javax.inject.Inject

class CredentialRepository @Inject constructor(private val apiService: AngloamericanApiService) :
    ICredentialRepository {
    override suspend fun getCredential(workerId: String): WorkerCredential {
        val response = apiService.getCredential(workerId)
        return response.data
    }
    override suspend fun getAuthAreas(workerId: String): List<AuthorizedAreas> {
        val response = apiService.getCredentialAuthorizedAreas(workerId)
        Log.i("responsedata", "${response}");
        return response.data
    }

    override suspend fun getAuthDivisions(workerId: String): List<AuthorizedDivisions> {
        val response = apiService.getCredentialAuthorizedDivisions(workerId)
        Log.i("responsedata", "${response}");
        return response.data
    }
}