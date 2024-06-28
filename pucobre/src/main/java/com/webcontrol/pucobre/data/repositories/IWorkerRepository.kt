package com.webcontrol.pucobre.data.repositories

import com.webcontrol.pucobre.data.model.WorkerPucobre

interface IWorkerRepository {
    suspend fun getWorker(workerId: String): WorkerPucobre?
}