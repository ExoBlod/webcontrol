package com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.model.ApproveMovementRequest
import com.webcontrol.angloamerican.data.model.DenyMovementRequest
import com.webcontrol.angloamerican.domain.usecases.approvemovements.movementdetail.MovementDetailUseCase
import com.webcontrol.angloamerican.ui.approvemovements.ui.allmovements.AllMovementsUIEvent
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovementDetailViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    val movementDetailUseCase: MovementDetailUseCase
) : ViewModel() {

    private val _movementDetail = MutableSharedFlow<MovementDetailUIEvent>()
    val movementDetail get() = _movementDetail.asSharedFlow()

    private val _approveMovement = MutableSharedFlow<ApproveMovementUIEvent>()
    val approveMovement get() = _approveMovement.asSharedFlow()

    private val _denyMovement = MutableSharedFlow<DenyMovementUIEvent>()
    val denyMovement get() = _denyMovement.asSharedFlow()

    fun getMovementDetail(batchId: String) = launch {
        _movementDetail.emit(MovementDetailUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                movementDetailUseCase.getMovementDetail(batchId)
            }.onSuccess { movement ->
                _movementDetail.emit(MovementDetailUIEvent.Success(movement))
                _movementDetail.emit(MovementDetailUIEvent.HideLoading)
            }.onFailure {
                _movementDetail.emit(MovementDetailUIEvent.Error)
                _movementDetail.emit(MovementDetailUIEvent.HideLoading)
            }
        }
    }

    fun approveMovement(movement: ApproveMovementRequest) = launch {
        _approveMovement.emit(ApproveMovementUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                movementDetailUseCase.approveMovement(movement)
            }.onSuccess {
                _approveMovement.emit(ApproveMovementUIEvent.HideLoading)
                _approveMovement.emit(ApproveMovementUIEvent.Success)
            }.onFailure {
                _approveMovement.emit(ApproveMovementUIEvent.HideLoading)
                _approveMovement.emit(ApproveMovementUIEvent.Error(it.message))
            }
        }
    }

    fun denyMovement(movement: DenyMovementRequest) = launch {
        _denyMovement.emit(DenyMovementUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                movementDetailUseCase.denyMovement(movement)
            }.onSuccess {
                _denyMovement.emit(DenyMovementUIEvent.HideLoading)
                _denyMovement.emit(DenyMovementUIEvent.Success)
            }.onFailure {
                _denyMovement.emit(DenyMovementUIEvent.HideLoading)
                _denyMovement.emit(DenyMovementUIEvent.Error)
            }
        }
    }
}
