package com.webcontrol.android.angloamerican.ui.credentialVirtual

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.AngloRepository
import com.webcontrol.android.ui.common.GenericViewState
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.SafeMediatorLiveData
import com.webcontrol.android.data.network.dto.CredentialVirtualDTO
import com.webcontrol.android.data.network.CredentialVirtualRequest
import kotlinx.coroutines.launch

class QuellavecoCredentialVirtualViewModel (
    private val repository: AngloRepository
): ViewModel()  {
    private val credentialVirtualDTOState =
        SafeMediatorLiveData(initialValue = GenericViewState<CredentialVirtualDTO>()).apply { }
    fun credentialVirtualState(): LiveData<GenericViewState<CredentialVirtualDTO>> = credentialVirtualDTOState

    fun getCredentialVirtual(workerId: CredentialVirtualRequest) {
        viewModelScope.launch {
            runCatching {
                credentialVirtualDTOState.update(isLoading = true, data = null, error = null)
                repository.getCredentialVirtual(workerId)
            }.onSuccess { dataCredential ->
                credentialVirtualDTOState.update(isLoading = false, data = dataCredential, error = null)
            }.onFailure {
                it.printStackTrace()
                credentialVirtualDTOState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }
}