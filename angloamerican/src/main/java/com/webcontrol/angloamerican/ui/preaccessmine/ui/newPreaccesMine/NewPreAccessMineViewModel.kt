package com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.model.LocalRequest
import com.webcontrol.angloamerican.data.model.ParkingUsage
import com.webcontrol.angloamerican.data.model.VehicleRequest
import com.webcontrol.angloamerican.data.model.WorkerRequest
import com.webcontrol.angloamerican.domain.usecases.GetHistoryBookCoursesUseCase
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.*
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPreAccessMineViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getHistoryBookCoursesUseCase: GetHistoryBookCoursesUseCase,
    private val getDivisionUseCase: GetDivisionUseCase,
    private val getLocalUseCase: GetLocalUseCase,
    private val getVehicleUseCase: GetVehicleUseCase,
    private val postParkingUseCase: PostParkingUseCase,
    private val getCheckListUseCase: GetCheckListUseCase,
    private val postInsertCheckListUseCase: PostInsertCheckListUseCase,
    private val insertPreaccessMineUseCase: InsertPreaccessMineUseCase,
    private val insertPreaccessDetailMineUseCase: InsertPreaccessDetailMineUseCase,
    private val getWorkerInfoUseCase: GetWorkerInfoUseCase,
    private val getValidationByWorkerIdUseCase: GetValidationByWorkerIdUseCase,
    private val postReservationPreaccessMineUseCase: PostReservationPreaccessMineUseCase,

) : ViewModel() {
    private val _listHistory = MutableSharedFlow<NewPreAccessMineUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()

    fun getDivisions() = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getDivisionUseCase()
            }.onSuccess { listDivision ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessDivision(listDivision))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun getLocals(localRequest: LocalRequest) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getLocalUseCase(localRequest)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessLocal(listLocal))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun getVehicle(vehicleRequest: VehicleRequest) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getVehicleUseCase(vehicleRequest)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessVehicle(listLocal))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun insertParking(parkingUsage: ParkingUsage) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postParkingUseCase(parkingUsage)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.Success(listLocal.toString()))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun insertPreaccessMine(preaccessMine: PreaccesoMina) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                insertPreaccessMineUseCase(preaccessMine)
            }.onSuccess { idPreaccess ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessInsertPreAcceso(idPreaccess))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun getChecklist(type: NewPreAccessMineFragment.Companion.ChecklistType, workerId: String, vehicleId: String, date: String) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getCheckListUseCase(type,workerId,vehicleId,date)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.Success(listLocal.toString()))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun getChecklistDetail(idDb: Int?) {

    }

    fun getWorkerInfo(request: WorkerRequest)  = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getWorkerInfoUseCase(request)
            }.onSuccess {
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessWorkerInfo(it))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun postCheckList(checkListTest: CheckListTest) = launch {
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postInsertCheckListUseCase(checkListTest)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.Success(listLocal.toString()))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)
    }

    fun insertPreaccessDetail(preaccesoDetalleMina: PreaccesoDetalleMina) = launch{
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                insertPreaccessDetailMineUseCase(preaccesoDetalleMina)
            }.onSuccess { listLocal ->
                _listHistory.emit(NewPreAccessMineUIEvent.Success(listLocal.toString()))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)

    }

   fun getValidationByWorkerId(WorkerId: String, LocalId: String) = launch{
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getValidationByWorkerIdUseCase(WorkerId, LocalId)
            }.onSuccess { validate ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)

    }

    fun postReservationPreaccessMine() = launch{
        _listHistory.emit(NewPreAccessMineUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postReservationPreaccessMineUseCase()
            }.onSuccess { validate ->
                _listHistory.emit(NewPreAccessMineUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(NewPreAccessMineUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(NewPreAccessMineUIEvent.HideLoading)

    }

}