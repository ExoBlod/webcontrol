package com.webcontrol.angloamerican.domain.usecases.approvemovements.approveruser

import com.webcontrol.angloamerican.data.model.Movement
import com.webcontrol.angloamerican.domain.repository.approvemovements.approveruser.IAllMovementsRepository
import javax.inject.Inject


class GetAllMovementsUseCase @Inject constructor(private val repository: IAllMovementsRepository) {

    suspend operator fun invoke(workerId: String): List<Movement> = repository.getAllMovements(workerId)
}