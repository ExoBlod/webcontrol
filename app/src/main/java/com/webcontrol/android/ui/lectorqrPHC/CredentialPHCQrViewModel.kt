package com.webcontrol.android.ui.lectorqrPHC

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.PHCRepository
import com.webcontrol.android.data.model.WorkerCredentialPHC
import com.webcontrol.android.ui.common.GenericViewStatePHC
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.SafeMediatorLiveData
import kotlinx.coroutines.launch

class CredentialPHCQrViewModel(
    private val phcRepository: PHCRepository
) : ViewModel() {
    private val _credential = SafeMediatorLiveData(initialValue = GenericViewStatePHC<WorkerCredentialPHC>()).apply { }

    fun credential(): LiveData<GenericViewStatePHC<WorkerCredentialPHC>> = _credential

    fun getCredential(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _credential.update(isLoading = true, data = null, error = null)
                phcRepository.getCredentials(workerId)
            }.onSuccess { credential ->
                _credential.update(isLoading = false, data = credential, error = null)
            }.onFailure {
                _credential.update(isLoading = false, data = null, error = it.message)
            }
        }
    }
}