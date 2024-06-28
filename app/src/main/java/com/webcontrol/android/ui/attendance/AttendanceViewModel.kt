package com.webcontrol.android.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.model.Attendance
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.network.AttendanceRequest
import com.webcontrol.android.data.network.AttendanceResponse
import com.webcontrol.android.ui.common.GenericViewState
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SafeMediatorLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor() : ViewModel() {

    private val api = RestClient.buildBarrick()

    private val attendanceHstState = SafeMediatorLiveData(initialValue = GenericViewState<ArrayList<Attendance>>()).apply { }
    private val divisionListState = SafeMediatorLiveData(initialValue = GenericViewState<ArrayList<Division>>()).apply { }
    private val attendanceResultState = SafeMediatorLiveData(initialValue = GenericViewState<AttendanceResponse>()).apply {  }

    fun attendanceHstState(): LiveData<GenericViewState<ArrayList<Attendance>>> = attendanceHstState
    fun divisionListState(): LiveData<GenericViewState<ArrayList<Division>>> = divisionListState
    fun attendanceResultState(): LiveData<GenericViewState<AttendanceResponse>> = attendanceResultState


    fun getAttendanceHst(workerId: String) {
        viewModelScope.launch {
            runCatching {
                attendanceHstState.update(data = null, isLoading = true, error = null)
                api.getAttendanceHst(workerId)
            }.onSuccess {
                attendanceHstState.update(data = it, isLoading = false, error = null)
            }.onFailure(::handleAttendanceHstFailure)
        }
    }

    fun getDivisionList(workerId: String) {
        viewModelScope.launch {
            runCatching {
                divisionListState.update(data = null, isLoading = true, error = null)
                api.getDivisions(workerId)
            }.onSuccess {
                divisionListState.update(data = it, isLoading = false, error = null)
            }.onFailure (::handleDivisionListFailure)
        }
    }

    fun registerAttendance(request: AttendanceRequest) {
        viewModelScope.launch {
            runCatching {
                attendanceResultState.update(data = null, isLoading = true, error = null)
                api.registerAttendance(request)
            }.onSuccess {
                attendanceResultState.update(data = it, isLoading = false, error = null)
            }.onFailure (::handleAttendanceResultFailure)
        }
    }

    private fun handleAttendanceHstFailure(throwable: Throwable) {
        if (throwable is CancellationException) return
        throwable.printStackTrace()
        attendanceHstState.update(isLoading = false, error = throwable.message.toString(), data = null)
    }

    private fun handleDivisionListFailure(throwable: Throwable) {
        if (throwable is CancellationException) return
        throwable.printStackTrace()
        divisionListState.update(isLoading = false, error = throwable.message.toString(), data = null)
    }

    private fun handleAttendanceResultFailure(throwable: Throwable) {
        if (throwable is CancellationException) return
        throwable.printStackTrace()
        attendanceResultState.update(isLoading = false, error = throwable.message.toString(), data = null)
    }
}