package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerResponse
import javax.inject.Inject

class CredentialSearchUserRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
) {
    suspend fun getCredentialSearchUser(workerIdRequest: WorkerRequest): List<WorkerResponse>{
        val response = checkListPreUsoApi.getCredentialSearchUser(workerIdRequest)
        val result = requireNotNull(response?.data)
        return result
    }
}