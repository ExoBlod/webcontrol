package com.webcontrol.android.angloamerican.ui.reservabus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SelectReservaBusViewModel @Inject constructor(
    private val repository: ReservaBusRepository
) : ViewModel() {
    val busesState: MutableLiveData<Resource<List<ResponseReservaBus>>> =
        MutableLiveData<Resource<List<ResponseReservaBus>>>()

    val existReserveState: MutableLiveData<Resource<ReservaBus2?>> =
        MutableLiveData<Resource<ReservaBus2?>>()



    fun getBusesAvailable(request: RequestReservaBus) {
        viewModelScope.launch {
            runCatching {
                repository.getBusesAvailable(request)
            }.onSuccess {
                busesState.postValue(Resource.Success(it.data))
            }.onFailure {
                busesState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun checkIfExistReserve(progId: Long, workerId: String){
        viewModelScope.launch {
            runCatching {
                repository.existReserveBus(progId, workerId)
            }.onSuccess {
                existReserveState.postValue(Resource.Success(it))
            }.onFailure {
                existReserveState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }


}