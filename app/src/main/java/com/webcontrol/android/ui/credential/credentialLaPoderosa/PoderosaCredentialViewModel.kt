package com.webcontrol.android.ui.credential.credentialLaPoderosa

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.LaPoderosaRepository
import com.webcontrol.android.data.model.DocumentLaPoderosa
import com.webcontrol.android.data.model.WorkerLaPoderosa
import com.webcontrol.android.ui.common.GenericViewStateLaPoderosa
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.SafeMediatorLiveData
import kotlinx.coroutines.launch

class PoderosaCredentialViewModel(
    private val laPoderosaRepository: LaPoderosaRepository
) : ViewModel() {
    private val workerInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateLaPoderosa<WorkerLaPoderosa>()).apply { }
    private val AccessdocumentsInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateLaPoderosa<List<DocumentLaPoderosa>>()).apply { }
    private val DriverdocumentsInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateLaPoderosa<List<DocumentLaPoderosa>>()).apply { }

    fun workerInfoState(): LiveData<GenericViewStateLaPoderosa<WorkerLaPoderosa>> = workerInfoState
    fun AccessdocumentsInfoState(): LiveData<GenericViewStateLaPoderosa<List<DocumentLaPoderosa>>> =
        AccessdocumentsInfoState
    fun DriverdocumentsInfoState(): LiveData<GenericViewStateLaPoderosa<List<DocumentLaPoderosa>>> =
        DriverdocumentsInfoState

    fun getCredentialData(rut: String) {
        viewModelScope.launch {
            runCatching {
                workerInfoState.update(isLoading = true, data = null, error = null)
                laPoderosaRepository.getCredentials(rut)
            }.onSuccess { dataCredencial ->
                workerInfoState.update(isLoading = false, data = dataCredencial, error = null)
            }.onFailure {
                it.printStackTrace()
                workerInfoState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getDocumentsDataAccess(rut: String, pase: Int, flag: Boolean) {
        viewModelScope.launch {
            runCatching {
                when (flag) {
                    true ->
                        AccessdocumentsInfoState.update(isLoading = true, data = null, error = null)

                    false ->
                        DriverdocumentsInfoState.update(isLoading = true, data = null, error = null)
                }
                laPoderosaRepository.getDocumentos(rut, pase)
            }.onSuccess { data ->
                when (flag) {
                    true ->
                        AccessdocumentsInfoState.update(
                            isLoading = false,
                            data = data,
                            error = null
                        )

                    false ->
                        DriverdocumentsInfoState.update(
                            isLoading = false,
                            data = data,
                            error = null
                        )
                }
            }.onFailure {
                it.printStackTrace()
                when (flag) {
                    true ->
                        AccessdocumentsInfoState.update(
                            isLoading = false,
                            data = null,
                            error = it.message
                        )

                    false ->
                        DriverdocumentsInfoState.update(
                            isLoading = false,
                            data = null,
                            error = it.message
                        )
                }
            }
        }
    }
}