package com.webcontrol.angloamerican.domain.repository.approvemovements.movementdetail

import com.webcontrol.angloamerican.data.model.ApproveMovementRequest
import com.webcontrol.angloamerican.data.model.ApproveMovementResponse
import com.webcontrol.angloamerican.data.model.DenyMovementRequest
import com.webcontrol.angloamerican.data.model.DenyMovementResponse
import com.webcontrol.angloamerican.data.model.MovementDetail

interface IMovementDetail {
    suspend fun getMovementDetail(batchId: String): MovementDetail
    suspend fun approveMovement(request: ApproveMovementRequest): ApproveMovementResponse
    suspend fun denyMovement(request: DenyMovementRequest)
}