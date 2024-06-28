package com.webcontrol.collahuasi.domain.worker

interface IGetWorker {

    suspend operator fun invoke(workerId: String): List<Worker>
}

class GetWorker(private val workerRepository: IWorkerRepository) : IGetWorker {
    override suspend fun invoke(workerId: String): List<Worker> {
        return workerRepository.getWorker(workerId)
    }
}