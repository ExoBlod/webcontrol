package com.webcontrol.angloamerican.ui.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.model.WorkerAngloamerican
import com.webcontrol.angloamerican.domain.usecases.GetWorkerUseCase
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val getWorkerUseCase: GetWorkerUseCase
) : ViewModel() {
    private val _worker = MutableLiveData<Result<WorkerAngloamerican?>>()
    val worker: LiveData<Result<WorkerAngloamerican?>> = _worker
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