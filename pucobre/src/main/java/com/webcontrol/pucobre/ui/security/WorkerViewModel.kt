package com.webcontrol.pucobre.ui.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import com.webcontrol.pucobre.data.model.WorkerPucobre
import com.webcontrol.pucobre.domain.usecases.GetWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val getWorkerUseCase: GetWorkerUseCase
) : ViewModel() {
    private val _worker = MutableLiveData<Result<WorkerPucobre?>>()
    val worker: LiveData<Result<WorkerPucobre?>> = _worker
    fun getWorker(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _worker.postValue(Loading(true))
                getWorkerUseCase(workerId)
            }.onSuccess { worker ->
                _worker.postValue(Success(worker))
            }.onFailure {
                _worker.postValue(Error(it.message.toString()))
            }
        }
    }
}

