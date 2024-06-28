package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.model.WorkerAngloamerican

interface IWorkerRepository {
    suspend fun getWorker(workerId: String): WorkerAngloamerican?
}