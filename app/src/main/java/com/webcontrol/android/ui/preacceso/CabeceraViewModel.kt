package com.webcontrol.android.ui.preacceso

import androidx.lifecycle.*
import com.webcontrol.android.data.clientRepositories.AngloRepository
import com.webcontrol.android.data.clientRepositories.CollahuasiRepository
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.model.Local
import com.webcontrol.android.data.model.Vehiculo
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.model.enum.ChecklistType
import com.webcontrol.android.data.network.ActiveDispatchGuideRequest
import com.webcontrol.android.data.network.LocalRequest
import com.webcontrol.android.data.network.VehicleRequest
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.data.network.dto.ActiveDispatchGuideRequestDTO
import com.webcontrol.android.ui.common.GenericViewState
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SafeMediatorLiveData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CabeceraViewModel (
    private val client: String,
    private val repository: PreaccesoRepository,
    private val angloRepository: AngloRepository,
    private val collahuasiRepository: CollahuasiRepository,
) : ViewModel() {

    private val workerInfoState =
        SafeMediatorLiveData(initialValue = GenericViewState<WorkerAnglo?>()).apply { }
    private val divisionListState =
        SafeMediatorLiveData(initialValue = GenericViewState<List<Division>>()).apply { }
    private val localListState =
        SafeMediatorLiveData(initialValue = GenericViewState<List<Local>>()).apply { }
    private val vehicleState =
        SafeMediatorLiveData(initialValue = GenericViewState<Vehiculo?>()).apply { }
    private val dispatchGuideState = SafeMediatorLiveData(initialValue = GenericViewState<List<ActiveDispatchGuideRequestDTO>>()).apply { }

    fun workerInfoState(): LiveData<GenericViewState<WorkerAnglo?>> = workerInfoState
    fun divisionListState(): LiveData<GenericViewState<List<Division>>> = divisionListState
    fun localListState(): LiveData<GenericViewState<List<Local>>> = localListState
    fun vehicleState(): LiveData<GenericViewState<Vehiculo?>> = vehicleState
    fun dispatchGuideState():LiveData<GenericViewState<List<ActiveDispatchGuideRequestDTO>>> = dispatchGuideState

    val lastPreaccess: LiveData<Preacceso?> = repository.lastPreaccess.asLiveData()
    val checklistTFS = MutableLiveData<CheckListTest?>()
    val checklistTDV = MutableLiveData<CheckListTest?>()
    val checklistDetailTFS = MutableLiveData<CheckListTest_Detalle?>()

    val timeValue = MutableLiveData<String>()


    fun getDispatchGuideState(activeDispatchGuide :ActiveDispatchGuideRequest){
        viewModelScope.launch {
            runCatching {
                dispatchGuideState.update(isLoading = true, data = null, error = null)
                angloRepository.getDispatchGuide(activeDispatchGuide)
            }.onSuccess { dataDispatch ->
                dispatchGuideState.update(isLoading = false, data = dataDispatch, error = null)
            }.onFailure {
                it.printStackTrace()
                dispatchGuideState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun insertDispatchGuideState(){

    }


    fun getWorkerInfo(request: WorkerRequest) {
        viewModelScope.launch {
            runCatching {
                workerInfoState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getWorkerInfo(request)
                    Companies.CH.valor -> collahuasiRepository.getWorkerInfo(request)
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                workerInfoState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                workerInfoState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getDivisions() {
        viewModelScope.launch {
            runCatching {
                divisionListState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getDivisions()
                    Companies.CH.valor -> collahuasiRepository.getDivisions()
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                divisionListState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                divisionListState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getLocals(request: LocalRequest) {
        viewModelScope.launch {
            runCatching {
                localListState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getLocals(request)
                    Companies.CH.valor -> collahuasiRepository.getLocals(request)
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                localListState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                localListState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getVehicle(request: VehicleRequest) {
        viewModelScope.launch {
            runCatching {
                vehicleState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getVehicle(request)
                    Companies.CH.valor -> collahuasiRepository.getVehicle(request)
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                vehicleState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                vehicleState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getActiveDispatchGuide(request: VehicleRequest) {
        viewModelScope.launch {
            runCatching {
                vehicleState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getVehicle(request)
                    Companies.CH.valor -> collahuasiRepository.getVehicle(request)
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                vehicleState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                vehicleState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getChecklist(
        type: ChecklistType,
        workerId: String?,
        vehicleId: String?,
        date: String?
    ) {
        viewModelScope.launch {
            var checklist:CheckListTest? = if(type==ChecklistType.TDV){
                repository.getChecklistTDV(
                    type.name, workerId, vehicleId,date
                )
            } else{
                repository.getChecklist(
                    type.name, workerId, vehicleId, date
                )
            }
            // TODO CONSULTA REMOTA PERO PARA TDV 7 DIAS ANTES NO ESTA FUNCIONAL
            /*if (checklist == null && workerId != null && vehicleId != null && date != null) {
                val remoteChecklist = repository.getRemoteChecklist(
                    type.name, workerId, vehicleId, date
                )
                if (remoteChecklist != null) {
                    checklist = CheckListTest(
                        remoteChecklist.name,
                        remoteChecklist.date,
                        remoteChecklist.workerId,
                        remoteChecklist.vehicleId
                    )
                }
            }
             */
            when (type) {
                ChecklistType.TFS -> checklistTFS.postValue(checklist)
                ChecklistType.TDV -> checklistTDV.postValue(checklist)
                else -> {}
            }

        }
    }
    fun getChecklistDetail(
        idDb:Int?
    ) {
        viewModelScope.launch {
            val checklistDetail = repository.getChecklistDetail(idDb)
            checklistDetailTFS.postValue(checklistDetail)
        }
    }
    suspend fun insertPreaccess(preaccess: Preacceso): Long {
        return repository.insertPreaccess(preaccess)
    }

    fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalle) {
        viewModelScope.launch {
            repository.insertPreaccessDetail(preaccessDetail)
        }
    }

    fun insertChecklistTest(checkListTest: CheckListTest) {
        viewModelScope.launch {
            repository.insertChecklistTest(checkListTest)
        }
    }

    fun getTime() {
        viewModelScope.launch {
            runCatching {
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.getTime()
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                timeValue.postValue(it.data!!)
            }.onFailure {
                it.printStackTrace()
                val time = SimpleDateFormat("HH:mm")
                timeValue.postValue(time.format(Date()))
            }
        }
    }
}