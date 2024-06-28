package com.webcontrol.collahuasi.data.worker

import com.webcontrol.collahuasi.data.network.Api
import com.webcontrol.collahuasi.domain.worker.IWorkerRepository
import com.webcontrol.collahuasi.domain.worker.Worker

class WorkerRepository(private val api: Api) : IWorkerRepository {

    override suspend fun getWorker(workerId: String): List<Worker> {
        return api.getWorker(workerId)
    }
}