package com.webcontrol.android.ui.credential.credentialBambas

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.BambasRepository
import com.webcontrol.android.data.model.DocumentsBambas
import com.webcontrol.android.data.model.WorkerBambas
import com.webcontrol.android.ui.common.GenericViewStateBambas
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.SafeMediatorLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BambasCredentialViewModel (
    private val bambasRepository: BambasRepository
) : ViewModel() {
    private val workerInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateBambas<WorkerBambas>()).apply { }
    private val AccessdocumentsInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateBambas<List<DocumentsBambas>>()).apply { }
    private val DriverdocumentsInfoState =
        SafeMediatorLiveData(initialValue = GenericViewStateBambas<List<DocumentsBambas>>()).apply { }

    fun workerInfoState(): LiveData<GenericViewStateBambas<WorkerBambas>> = workerInfoState
    fun AccessdocumentsInfoState(): LiveData<GenericViewStateBambas<List<DocumentsBambas>>> = AccessdocumentsInfoState
    fun DriverdocumentsInfoState(): LiveData<GenericViewStateBambas<List<DocumentsBambas>>> = DriverdocumentsInfoState

    fun getCredentialData(rut: String) {
        viewModelScope.launch {
            runCatching {
                workerInfoState.update(isLoading = true, data = null, error = null)
                bambasRepository.getCredentials(rut)
            }.onSuccess { dataCredencial ->
                workerInfoState.update(isLoading = false, data = dataCredencial, error = null)
            }.onFailure {
                it.printStackTrace()
                workerInfoState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getDocumentsDataAccess(rut:String,pase:Int,flag:Boolean){
        viewModelScope.launch {
            runCatching {
                when (flag){
                    true ->
                        AccessdocumentsInfoState.update(isLoading = true, data = null, error = null)
                    false ->
                        DriverdocumentsInfoState.update(isLoading = true, data = null, error = null)
                }
                bambasRepository.getDocumentos(rut,pase)
            }.onSuccess { data ->
                when (flag){
                    true->
                        AccessdocumentsInfoState.update(isLoading = false, data = data, error = null)
                    false->
                        DriverdocumentsInfoState.update(isLoading = false, data = data, error = null)
                }
            }.onFailure {
                it.printStackTrace()
                when (flag){
                    true->
                        AccessdocumentsInfoState.update(isLoading = false, data = null, error = it.message)
                    false->
                        DriverdocumentsInfoState.update(isLoading = false, data = null, error = it.message)
                }
            }
        }
    }
    /*fun getDocumentsDataDriver(rut:String,pase:Int){
        viewModelScope.launch {
            runCatching {
                DriverdocumentsInfoState.update(isLoading = true, data = null, error = null)
                bambasRepository.getDocumentos(rut,pase)
            }.onSuccess { dataDocumentsDriver ->
                DriverdocumentsInfoState.update(isLoading = false, data = dataDocumentsDriver, error = null)
            }.onFailure {
                it.printStackTrace()
                DriverdocumentsInfoState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }*/
}