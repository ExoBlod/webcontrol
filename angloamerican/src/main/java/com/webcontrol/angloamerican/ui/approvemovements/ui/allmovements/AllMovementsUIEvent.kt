package com.webcontrol.angloamerican.ui.approvemovements.ui.allmovements

import com.webcontrol.angloamerican.data.model.Movement
import com.webcontrol.angloamerican.data.model.MovementDetail

sealed class AllMovementsUIEvent {
    data class Success(val allMovements: List<Movement>) : AllMovementsUIEvent()
    object Error : AllMovementsUIEvent()
    object ShowLoading : AllMovementsUIEvent()
    object HideLoading : AllMovementsUIEvent()
}