package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMinePassengers

import android.content.Context
import androidx.lifecycle.*
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.dto.RegControl
import com.webcontrol.angloamerican.data.model.WorkerRequest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine.NewPreAccessMineUIEvent
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.*
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreAccessMinePassengersViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getWorkerInfoUseCase: GetWorkerInfoUseCase,
    private val repository: IPreaccessMineRepository,
    private val insertPreaccessDetailMineUseCase: InsertPreaccessDetailMineUseCase,
    private val postReservationPreaccessDetailMineUseCase: PostReservationPreaccessDetailMineUseCase,
    private val postReservationPreaccessMineUseCase: PostReservationPreaccessMineUseCase,
    ) : ViewModel() {
    private val _listHistory = MutableSharedFlow<PreAccessMinePassengersUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()

    fun sendPreaccessRegList(request: WorkerRequest) = launch {
        _listHistory.emit(PreAccessMinePassengersUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getWorkerInfoUseCase(request)
            }.onSuccess {
                _listHistory.emit(PreAccessMinePassengersUIEvent.SuccessWorkerInfo(it))
            }.onFailure {
                _listHistory.emit(PreAccessMinePassengersUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMinePassengersUIEvent.HideLoading)
    }

    fun getWorkerInfo(request: WorkerRequest) = launch {
        _listHistory.emit(PreAccessMinePassengersUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getWorkerInfoUseCase(request)
            }.onSuccess {
                _listHistory.emit(PreAccessMinePassengersUIEvent.SuccessWorkerInfo(it))
            }.onFailure {
                _listHistory.emit(PreAccessMinePassengersUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMinePassengersUIEvent.HideLoading)
    }

    fun insertPreaccessDetail(preaccesoDetalleMina: PreaccesoDetalleMina) = launch{
        _listHistory.emit(PreAccessMinePassengersUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                insertPreaccessDetailMineUseCase(preaccesoDetalleMina)
            }.onSuccess { idPreAccessMine ->
                _listHistory.emit(PreAccessMinePassengersUIEvent.SuccessInsertPreaccessDetailMine(idPreAccessMine))
            }.onFailure {
                _listHistory.emit(PreAccessMinePassengersUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMinePassengersUIEvent.HideLoading)

    }

    fun postReservationPreaccessDetailMine() = launch{
        _listHistory.emit(PreAccessMinePassengersUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postReservationPreaccessDetailMineUseCase()
            }.onSuccess { validate ->
                _listHistory.emit(PreAccessMinePassengersUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(PreAccessMinePassengersUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMinePassengersUIEvent.HideLoading)

    }

    fun postReservationPreaccessMine() = launch{
        _listHistory.emit(PreAccessMinePassengersUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postReservationPreaccessMineUseCase()
            }.onSuccess { validate ->
                _listHistory.emit(PreAccessMinePassengersUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(PreAccessMinePassengersUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMinePassengersUIEvent.HideLoading)
    }

    fun deletePreaccess(preaccessId: Int) {
        viewModelScope.launch {
            repository.deletePreaccess(preaccessId)
        }
    }
}