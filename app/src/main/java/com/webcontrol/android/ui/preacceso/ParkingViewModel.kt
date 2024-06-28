package com.webcontrol.android.ui.preacceso

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.Parameter
import com.webcontrol.android.data.model.ParkingUsage
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ParkingResponse
import com.webcontrol.android.util.DateUtils
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val api: RestInterfaceAnglo
) : ViewModel() {
    val parkingCapacity: MutableLiveData<List<Parameter>?> = MutableLiveData()
    val availableParkingSpaces: MutableLiveData<ParkingResponse> = MutableLiveData()
    val insertParkingUsage: MutableLiveData<ApiResponseAnglo<Any>> = MutableLiveData()

    fun getParkingCapacity() {
        viewModelScope.launch {
            runCatching {
                api.getParameter("DISPONIBILIDAD_ESTACIONAMIENTOS")
            }.onSuccess {
                parkingCapacity.postValue(it.data)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun getAvailableParkingSpaces() {
        viewModelScope.launch {
            runCatching {
                api.getAvailableParkingSpaces(DateUtils.getDate("yyyyMMdd"))
            }.onSuccess {
                availableParkingSpaces.postValue(it.data[0])
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun insertParking(parkingUsage: ParkingUsage) {
        viewModelScope.launch {
            runCatching {
                api.insertParking(parkingUsage)
            }.onSuccess {
                insertParkingUsage.postValue(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}