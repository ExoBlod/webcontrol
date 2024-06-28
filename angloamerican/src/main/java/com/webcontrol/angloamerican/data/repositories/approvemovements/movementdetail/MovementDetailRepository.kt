package com.webcontrol.angloamerican.data.repositories.approvemovements.movementdetail


import com.webcontrol.angloamerican.data.model.ApproveMovementRequest
import com.webcontrol.angloamerican.data.model.ApproveMovementResponse
import com.webcontrol.angloamerican.data.model.DenyMovementRequest
import com.webcontrol.angloamerican.data.model.DenyMovementResponse
import com.webcontrol.angloamerican.data.model.MovementDetail
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.domain.repository.approvemovements.movementdetail.IMovementDetail
import javax.inject.Inject

class MovementDetailRepository @Inject constructor(private val apiService: AngloamericanApiService):
    IMovementDetail {

    override suspend fun getMovementDetail(batchId: String): MovementDetail {
        val response = apiService.getMovementDetail(batchId)
        if(response.data != null){
            val movementList = response.data as List<MovementDetail>
            return movementList.first()
        } else {
            throw NoSuchElementException("No se encontró el detalle de movimiento con loteID: $batchId")
        }
    }

    override suspend fun approveMovement(request: ApproveMovementRequest): ApproveMovementResponse {
        val response = apiService.approveMovement(request)

        val movementResponse = response.data?.firstOrNull()

        if (movementResponse != null) {
            when (movementResponse.message) {
                 APPROVE_MOVEMENT -> return movementResponse
                 DENY_MOVEMENT -> throw NoSuchElementException()
                else -> throw NoSuchElementException()
            }
        } else {
            throw NoSuchElementException()
        }
    }

    override suspend fun denyMovement(request: DenyMovementRequest) {
        apiService.denyMovement(request)
    }

    companion object{
        const val APPROVE_MOVEMENT = "PASE AUTORIZADO"
        const val DENY_MOVEMENT = "PASE NO AUTORIZADO"
    }
}