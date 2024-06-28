package com.webcontrol.pucobre.domain.usecases

import com.webcontrol.pucobre.data.repositories.IWorkerRepository
import javax.inject.Inject

class GetWorkerUseCase @Inject constructor(private val repository: IWorkerRepository) {
    suspend operator fun invoke(workerId: String) = repository.getWorker(workerId)
}