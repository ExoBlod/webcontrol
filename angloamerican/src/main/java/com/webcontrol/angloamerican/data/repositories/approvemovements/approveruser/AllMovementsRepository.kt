package com.webcontrol.angloamerican.data.repositories.approvemovements.approveruser

import com.webcontrol.angloamerican.data.model.Movement
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.domain.repository.approvemovements.approveruser.IAllMovementsRepository
import javax.inject.Inject


class AllMovementsRepository @Inject constructor(private val apiService: AngloamericanApiService): IAllMovementsRepository {

    override suspend fun getAllMovements(workerId: String): List<Movement> {
        val response = apiService.getAllMovements(workerId)
        if(response.data != null){
            return response.data as List<Movement>
        } else {
            throw NoSuchElementException("No se encontraron movimientos para el worker con workerID: $workerId")
        }
    }
}