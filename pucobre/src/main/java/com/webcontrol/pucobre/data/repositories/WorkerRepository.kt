package com.webcontrol.pucobre.data.repositories

import com.webcontrol.pucobre.data.PucobreApiService
import com.webcontrol.pucobre.data.model.WorkerPucobre
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    private val apiService: PucobreApiService
) : IWorkerRepository {
    override suspend fun getWorker(workerId: String): WorkerPucobre? {
        val response = apiService.getWorker(WorkerPucobre(workerId))
        return response.data
    }
}