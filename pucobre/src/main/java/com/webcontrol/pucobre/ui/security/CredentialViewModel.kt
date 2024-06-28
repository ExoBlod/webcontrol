package com.webcontrol.pucobre.ui.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import com.webcontrol.pucobre.data.model.WorkerCredential
import com.webcontrol.pucobre.domain.usecases.GetCredentialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialViewModel @Inject constructor(
    private val getCredentialUseCase: GetCredentialUseCase
) : ViewModel() {
    private val _credential = MutableLiveData<Result<WorkerCredential>?>()
    val credential: LiveData<Result<WorkerCredential>?> = _credential

    fun getCredential(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _credential.postValue(Loading(true))
                getCredentialUseCase(workerId)
            }.onSuccess { credential ->
                _credential.postValue(Success(credential) as Result<WorkerCredential>?)
            }.onFailure {
                _credential.postValue(Error(it.message.toString()))
            }
        }
    }
}