package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.data.model.WorkerAngloamerican
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    private val apiService: AngloamericanApiService
): IWorkerRepository{
    override suspend fun getWorker(workerId: String): WorkerAngloamerican? {
        val response = apiService.getWorker(WorkerAngloamerican(workerId))
        return response.data
    }
}