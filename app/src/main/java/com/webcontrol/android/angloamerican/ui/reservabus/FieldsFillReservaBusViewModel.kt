package com.webcontrol.android.angloamerican.ui.reservabus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.data.model.ParametroViaje
import com.webcontrol.android.data.model.DestinyReservaBus
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.model.SourceReservaBus
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.synthetic.main.content_acceso_header.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class FieldsFillReservaBusViewModel @Inject constructor(
    private val repository: ReservaBusRepository
) : ViewModel() {

    val sourceState: MutableLiveData<Resource<List<SourceReservaBus>>> =
        MutableLiveData<Resource<List<SourceReservaBus>>>()
    val destinyState: MutableLiveData<Resource<List<DestinyReservaBus>>> =
        MutableLiveData<Resource<List<DestinyReservaBus>>>()
    val divitionState: MutableLiveData<Resource<List<Division>>> =
        MutableLiveData<Resource<List<Division>>>()

    val workerInfoState: MutableLiveData<Resource<WorkerAnglo>> =
        MutableLiveData<Resource<WorkerAnglo>>()


    fun getDestinies(divition: String) {
        viewModelScope.launch {
            runCatching {
                repository.getDestiniesReservaBus(divition)
            }.onSuccess {
                destinyState.postValue(Resource.Success(it.data))
            }.onFailure {
                destinyState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
    fun getDivitions() {
        viewModelScope.launch {
            runCatching {
                repository.getDivitions()
            }.onSuccess {
                divitionState.postValue(Resource.Success(it.data))
            }.onFailure {
                divitionState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
    fun getSources(divition: String) {
        viewModelScope.launch {
            runCatching {
                repository.getSourcesReservaBus(divition)
            }.onSuccess {
                sourceState.postValue(Resource.Success(it.data))
            }.onFailure {
                sourceState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun getWorkerInfo(request: WorkerRequest) {
        viewModelScope.launch {
            runCatching {
                repository.getWorkerInfo(request)
            }.onSuccess {
                workerInfoState.postValue(Resource.Success(it.data!!))
            }.onFailure {
                workerInfoState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }



}