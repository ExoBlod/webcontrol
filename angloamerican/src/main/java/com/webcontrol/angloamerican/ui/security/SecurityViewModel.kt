package com.webcontrol.angloamerican.ui.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetTokenUseCase
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel  @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase
): ViewModel(){
    private val _token = MutableLiveData<Result<String>>()
    val token: LiveData<Result<String>> = _token

    fun getToken(workerId: String) {
        viewModelScope.launch {
            runCatching {
                _token.postValue(Loading(true))
                getTokenUseCase(workerId)
            }.onSuccess { token ->
                _token.postValue(Success(token))
            }.onFailure {
                _token.postValue(Error(it.message.toString()))
            }
        }
    }
}