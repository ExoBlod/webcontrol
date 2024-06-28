package com.webcontrol.angloamerican.ui.security

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.network.response.AuthorizedAreas
import com.webcontrol.angloamerican.data.network.response.AuthorizedDivisions
import com.webcontrol.angloamerican.data.network.response.WorkerCredential
import com.webcontrol.angloamerican.domain.usecases.AuthorizedAreasUseCase
import com.webcontrol.angloamerican.domain.usecases.AuthorizedDivisionsUseCase
import com.webcontrol.angloamerican.domain.usecases.GetCredentialUseCase
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialViewModel @Inject constructor(
    private val getCredentialUseCase: GetCredentialUseCase,
    private val getAuthorizedAreasUseCase: AuthorizedAreasUseCase,
    private val getAuthorizedDivisionsUseCase: AuthorizedDivisionsUseCase
) : ViewModel() {
    private val _credential = MutableLiveData<Result<WorkerCredential>>()
    val credential: LiveData<Result<WorkerCredential>> = _credential

    private val _authAreas = MutableLiveData<Result<List<AuthorizedAreas>>>()
    val authAreas: LiveData<Result<List<AuthorizedAreas>>> = _authAreas

    private val _authDivisions = MutableLiveData<Result<List<AuthorizedDivisions>>>()
    val authDivisions: LiveData<Result<List<AuthorizedDivisions>>> = _authDivisions

    fun getCredential(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _credential.postValue(Loading(true))
                getCredentialUseCase(workerId)
            }.onSuccess { credential ->
                _credential.postValue(Success(credential))
            }.onFailure {
                _credential.postValue(Error(it.message.toString()))
            }
        }
    }

    fun getAuthorizedAreas(workerId: String){
        viewModelScope.launch {
           runCatching {
               _authAreas.postValue(Loading(true))
               getAuthorizedAreasUseCase(workerId)

           }.onSuccess { authArea ->
               _authAreas.postValue(Success(authArea))
           }.onFailure {
               _authAreas.postValue(Error(it.message.toString()))
           }
        }
    }
    fun getAuthorizedDivisions(workerId: String){
        viewModelScope.launch {
           runCatching {
               _authDivisions.postValue(Loading(true))
               getAuthorizedDivisionsUseCase(workerId)

           }.onSuccess { authDivision ->
               _authDivisions.postValue(Success(authDivision))
           }.onFailure {
               _authDivisions.postValue(Error(it.message.toString()))
           }
        }
    }


}