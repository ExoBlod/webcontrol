package com.webcontrol.android.angloamerican.ui.reservabus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleReservaViewModel @Inject constructor(
    private val repository: ReservaBusRepository
) : ViewModel() {
    val cancelReserveState: MutableLiveData<Resource<MutableList<ResponseReservaBus>>> =
        MutableLiveData<Resource<MutableList<ResponseReservaBus>>>()

    fun cancelReserveBus(request: RequestReservaBus){
        viewModelScope.launch {
            runCatching {
                repository.cancelReserveBus(request)
            }.onSuccess {
                cancelReserveState.postValue(Resource.Success(it.data))
            }.onFailure {
                cancelReserveState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}