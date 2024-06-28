package com.webcontrol.collahuasi.presentation.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.collahuasi.domain.authentication.AuthenticationRequest
import com.webcontrol.collahuasi.domain.authentication.AuthenticationResponse
import com.webcontrol.collahuasi.domain.authentication.IAuthenticate
import com.webcontrol.core.common.model.Resource
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val useCase: IAuthenticate) : ViewModel() {

    val viewState = MutableLiveData<Resource<AuthenticationResponse>>()

    fun authenticate(request: AuthenticationRequest) {
        viewModelScope.launch {
            runCatching {
                viewState.postValue(Resource.Loading(true))
                useCase(request)
            }.onSuccess {
                viewState.postValue(Resource.Success(it))
            }.onFailure {
                viewState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}