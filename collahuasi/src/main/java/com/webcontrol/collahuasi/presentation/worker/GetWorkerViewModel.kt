package com.webcontrol.collahuasi.presentation.worker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.collahuasi.domain.worker.IGetWorker
import com.webcontrol.collahuasi.domain.worker.Worker
import com.webcontrol.core.common.model.Resource
import kotlinx.coroutines.launch

class GetWorkerViewModel(private val useCase: IGetWorker) : ViewModel() {

    val viewState = MutableLiveData<Resource<List<Worker>>>()

    fun getWorker(workerId: String) {
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