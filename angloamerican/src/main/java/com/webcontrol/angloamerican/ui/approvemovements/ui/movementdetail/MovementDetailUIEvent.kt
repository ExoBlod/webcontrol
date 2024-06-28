package com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail

import com.webcontrol.angloamerican.data.model.MovementDetail

sealed class MovementDetailUIEvent {
    data class Success(val movement: MovementDetail) : MovementDetailUIEvent()
    object Error : MovementDetailUIEvent()
    object ShowLoading : MovementDetailUIEvent()
    object HideLoading : MovementDetailUIEvent()
}