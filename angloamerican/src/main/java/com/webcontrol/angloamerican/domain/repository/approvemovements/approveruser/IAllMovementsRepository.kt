package com.webcontrol.angloamerican.domain.repository.approvemovements.approveruser

import com.webcontrol.angloamerican.data.model.Movement


interface IAllMovementsRepository {
    suspend fun getAllMovements(workerId: String): List<Movement>
}