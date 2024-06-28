package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.network.response.AuthorizedAreas
import com.webcontrol.angloamerican.data.network.response.AuthorizedDivisions
import com.webcontrol.angloamerican.data.network.response.WorkerCredential

interface ICredentialRepository {
    suspend fun getCredential(workerId: String): WorkerCredential
    suspend fun getAuthAreas(workerId: String): List<AuthorizedAreas>
    suspend fun getAuthDivisions(workerId: String): List<AuthorizedDivisions>
}