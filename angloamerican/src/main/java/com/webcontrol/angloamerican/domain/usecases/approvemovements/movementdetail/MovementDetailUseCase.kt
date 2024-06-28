package com.webcontrol.angloamerican.domain.usecases.approvemovements.movementdetail

import com.webcontrol.angloamerican.data.model.ApproveMovementRequest
import com.webcontrol.angloamerican.data.model.ApproveMovementResponse
import com.webcontrol.angloamerican.data.model.DenyMovementRequest
import com.webcontrol.angloamerican.data.model.DenyMovementResponse
import com.webcontrol.angloamerican.data.model.MovementDetail
import com.webcontrol.angloamerican.domain.repository.approvemovements.movementdetail.IMovementDetail
import javax.inject.Inject

class MovementDetailUseCase @Inject constructor(private val repository: IMovementDetail) {

    suspend fun getMovementDetail(batchId: String): MovementDetail = repository.getMovementDetail(batchId)

    suspend fun approveMovement(request: ApproveMovementRequest): ApproveMovementResponse = repository.approveMovement(request)

    suspend fun denyMovement(request: DenyMovementRequest) = repository.denyMovement(request)
}