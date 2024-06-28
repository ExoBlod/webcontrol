package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class GetValidationByWorkerIdUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(WorkerId: String, LocalId: String) = repository.getValidationByWorkerId(WorkerId,LocalId)
}