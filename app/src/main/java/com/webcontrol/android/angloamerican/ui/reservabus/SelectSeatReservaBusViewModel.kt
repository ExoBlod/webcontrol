package com.webcontrol.android.angloamerican.ui.reservabus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.angloamerican.data.ResponseSeatBus
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SelectSeatReservaBusViewModel @Inject constructor(
    private val repository: ReservaBusRepository
) : ViewModel() {
    val seatsState: MutableLiveData<Resource<MutableList<ResponseSeatBus>>> =
        MutableLiveData<Resource<MutableList<ResponseSeatBus>>>()

    val reserveState: MutableLiveData<Resource<MutableList<ResponseReservaBus>>> =
        MutableLiveData<Resource<MutableList<ResponseReservaBus>>>()

    fun getSeatsBus(request: RequestReservaBus) {
        viewModelScope.launch {
            runCatching {
                repository.getSeatsBus(request)
            }.onSuccess {
                seatsState.postValue(Resource.Success(it.data))
            }.onFailure {
                seatsState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun makeReservationBus(request: RequestReservaBus){
        viewModelScope.launch {
            runCatching {
                repository.reserveBus(request)
            }.onSuccess {
                reserveState.postValue(Resource.Success(it.data))
            }.onFailure {
                reserveState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}