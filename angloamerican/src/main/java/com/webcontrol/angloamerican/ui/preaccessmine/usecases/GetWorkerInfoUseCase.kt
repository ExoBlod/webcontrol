package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.model.WorkerRequest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class GetWorkerInfoUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(request: WorkerRequest) = repository.getWorkerInfo(request)
}