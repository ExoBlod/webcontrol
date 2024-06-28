package com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail

import com.webcontrol.angloamerican.data.model.MovementDetail

sealed class DenyMovementUIEvent {
    object Success : DenyMovementUIEvent()
    object Error : DenyMovementUIEvent()
    object ShowLoading : DenyMovementUIEvent()
    object HideLoading : DenyMovementUIEvent()
}