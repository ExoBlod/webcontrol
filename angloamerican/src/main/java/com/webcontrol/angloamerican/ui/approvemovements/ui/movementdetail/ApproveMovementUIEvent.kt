package com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail

import com.webcontrol.angloamerican.data.model.MovementDetail

sealed class ApproveMovementUIEvent {
    object Success : ApproveMovementUIEvent()
    class Error(val message: String?) : ApproveMovementUIEvent()
    object ShowLoading : ApproveMovementUIEvent()
    object HideLoading : ApproveMovementUIEvent()
}