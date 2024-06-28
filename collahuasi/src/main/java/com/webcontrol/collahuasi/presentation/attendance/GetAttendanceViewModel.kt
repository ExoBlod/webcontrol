package com.webcontrol.collahuasi.presentation.attendance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.collahuasi.domain.attendance.Attendance
import com.webcontrol.collahuasi.domain.attendance.IGetAttendance
import com.webcontrol.core.common.model.Resource
import kotlinx.coroutines.launch

class GetAttendanceViewModel(private val useCase: IGetAttendance) : ViewModel() {

    val viewState = MutableLiveData<Resource<List<Attendance>>>()

    fun getAttendance(workerId: String) {
        viewModelScope.launch {
            runCatching {
                viewState.postValue(Resource.Loading(true))
                useCase(workerId)
            }.onSuccess {
                viewState.postValue(Resource.Success(it))
            }.onFailure {
                viewState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}