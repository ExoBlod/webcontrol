package com.webcontrol.pucobre.ui.security

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import com.webcontrol.pucobre.domain.usecases.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase
) : ViewModel() {
    private val _token = MutableLiveData<Result<String>>()
    val token: LiveData<Result<String>> = _token

    fun getToken(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _token.postValue(Loading(true))
                Log.i("runCatching","PC Se esta ejecutando")
                getTokenUseCase(workerId)
            }.onSuccess { token ->
                _token.postValue(Success(token))
                Log.i("onSuccess","PC Llego al token")
            }.onFailure {
                _token.postValue(Error(it.message.toString()))
                Log.i("onFailure",it.toString())
            }
        }
    }
}