package com.webcontrol.collahuasi.domain.worker

interface IWorkerRepository {

    suspend fun getWorker(workerId: String): List<Worker>
}