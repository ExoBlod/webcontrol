package com.webcontrol.angloamerican.ui.preaccessmine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.model.WorkerRequest
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import com.webcontrol.angloamerican.ui.preaccessmine.models.WorkerInfo
import com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine.NewPreAccessMineUIEvent
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.GetWorkerInfoUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreAccessMineViewModel @Inject
constructor(
) : ViewModel() {
    private val _workerIdLiveData = MutableLiveData<WorkerInfo>()
    private val workerIdLiveData: LiveData<WorkerInfo>
        get() = _workerIdLiveData

    private val _preAccessMineIdLiveData = MutableLiveData<Long>()
    private val preAccessMineIdLiveData: LiveData<Long>
        get() = _preAccessMineIdLiveData

    val workerId: WorkerInfo get() = workerIdLiveData.value?:WorkerInfo()
    val preAccessMineId: Long get() = preAccessMineIdLiveData.value?:0L

    fun setWorkerId(workerInfo: WorkerInfo) {
        _workerIdLiveData.value = workerInfo
    }

    fun setPreAccessMineId(id: Long) {
        _preAccessMineIdLiveData.value = id
    }

}