package com.webcontrol.android.angloamerican.ui.credentialLicencia

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.AngloRepository
import com.webcontrol.android.data.network.dto.AuthorizationPlacesDTO
import com.webcontrol.android.data.network.dto.AuthorizedVehicleDTO
import com.webcontrol.android.data.network.dto.DataWorkerDTO
import com.webcontrol.android.data.network.dto.DocumentValidityDTO
import com.webcontrol.android.ui.common.GenericViewState
import com.webcontrol.android.ui.common.update
import com.webcontrol.android.util.SafeMediatorLiveData
import kotlinx.coroutines.launch

class QuellavecoCredentialLicenciaViewModel(
    private val angloRepository: AngloRepository
): ViewModel() {

    private val workerInfoState =
        SafeMediatorLiveData(initialValue = GenericViewState<List<DataWorkerDTO>>()).apply { }
    private val authorizationPlaces =
        SafeMediatorLiveData(initialValue = GenericViewState<List<AuthorizationPlacesDTO>>()).apply { }
    private val authorizationVehicle =
        SafeMediatorLiveData(initialValue = GenericViewState<List<AuthorizedVehicleDTO>>()).apply { }
    private val documents =
        SafeMediatorLiveData(initialValue = GenericViewState<List<DocumentValidityDTO>>()).apply { }

    fun workerInfoState(): LiveData<GenericViewState<List<DataWorkerDTO>>> = workerInfoState

    fun AuthorizationPlaces(): LiveData<GenericViewState<List<AuthorizationPlacesDTO>>> = authorizationPlaces

    fun AuthorizationVehicle(): LiveData<GenericViewState<List<AuthorizedVehicleDTO>>> = authorizationVehicle

    fun documents(): LiveData<GenericViewState<List<DocumentValidityDTO>>> = documents

    fun getLicenseData(rut: String) {
        viewModelScope.launch {
            runCatching {
                workerInfoState.update(isLoading = true, data = null, error = null)
                angloRepository.getLicenseData(rut)
            }.onSuccess { dataVehicle ->
                workerInfoState.update(isLoading = false, data = dataVehicle, error = null)
            }.onFailure {
                it.printStackTrace()
                workerInfoState.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getAuthorizationPlaces(rut: String) {
        viewModelScope.launch {
            runCatching {
                authorizationPlaces.update(isLoading = true, data = null, error = null)
                angloRepository.getAuthorizationPlaces(rut)
            }.onSuccess { authorizationPlace ->
                authorizationPlaces.update(isLoading = false, data = authorizationPlace, error = null)
            }.onFailure {
                it.printStackTrace()
                authorizationPlaces.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getAuthorizationVehicle(rut: String) {
        viewModelScope.launch {
            runCatching {
                authorizationVehicle.update(isLoading = true, data = null, error = null)
                angloRepository.getAuthorizationVehicle(rut)
            }.onSuccess { authorizationVehicles ->
                authorizationVehicle.update(isLoading = false, data = authorizationVehicles, error = null)
            }.onFailure {
                it.printStackTrace()
                authorizationVehicle.update(isLoading = false, data = null, error = it.message)
            }
        }
    }

    fun getDocuments(rut: String) {
        viewModelScope.launch {
            runCatching {
                documents.update(isLoading = true, data = null, error = null)
                angloRepository.getDocuments(rut)
            }.onSuccess { listDocuments ->
                documents.update(isLoading = false, data = listDocuments, error = null)
            }.onFailure {
                it.printStackTrace()
                documents.update(isLoading = false, data = null, error = it.message)
            }
        }
    }
}