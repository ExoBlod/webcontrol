package com.webcontrol.android.ui.preacceso

import androidx.lifecycle.*
import com.webcontrol.android.data.clientRepositories.AngloRepository
import com.webcontrol.android.data.clientRepositories.CollahuasiRepository
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.RegControl
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.ui.common.GenericViewState
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SafeMediatorLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class PassengerViewModel (
    private val client: String,
    private val repository: PreaccesoRepository,
    private val angloRepository: AngloRepository,
    private val collahuasiRepository: CollahuasiRepository
) : ViewModel() {
    private val workerInfoState = SafeMediatorLiveData(initialValue = GenericViewState<WorkerAnglo?>()).apply {  }
    private val sendPreaccessState = SafeMediatorLiveData(initialValue = GenericViewState<Any>()).apply {  }

    fun workerInfoState(): LiveData<GenericViewState<WorkerAnglo?>> = workerInfoState
    fun sendPreaccessState(): LiveData<GenericViewState<Any>> = sendPreaccessState

    val preaccessDetailList: MutableLiveData<List<PreaccesoDetalle>> = MutableLiveData()
    val preaccessRegList: MutableLiveData<List<RegControl>> = MutableLiveData()
    val lastPreaccess: LiveData<Preacceso?> = repository.lastPreaccess.asLiveData()


    fun getPreaccessDetailList(preaccessId: Int) = repository.getPreaccessDetailList(preaccessId).asLiveData()

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

    fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalle) {
        viewModelScope.launch {
            repository.insertPreaccessDetail(preaccessDetail)
        }
    }

    fun getPreaccessRegList(preaccessId: Int) {
        viewModelScope.launch {
            preaccessRegList.postValue(repository.getPreaccessControlList(preaccessId))
        }
    }

    fun sendPreaccessRegList(list: List<RegControl>) {
        viewModelScope.launch {
            runCatching {
                sendPreaccessState.update(isLoading = true, data = null, error = null)
                when (client) {
                    Companies.ANGLO.valor -> angloRepository.sendPreaccessControlList(list)
                    Companies.CH.valor -> collahuasiRepository.sendPreaccessControlList(list)
                    else -> throw IllegalArgumentException("Client $client not implemented")
                }
            }.onSuccess {
                sendPreaccessState.update(isLoading = false, data = it, error = null)
            }.onFailure {
                it.printStackTrace()
                sendPreaccessState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun updatePreaccessStatus(preaccessId: Int) {
        viewModelScope.launch {
            repository.updatePreaccessStatus(preaccessId)
        }
    }

    fun deletePreaccess(preaccess: Preacceso) {
        viewModelScope.launch {
            repository.deletePreaccess(preaccess)
        }
    }
}