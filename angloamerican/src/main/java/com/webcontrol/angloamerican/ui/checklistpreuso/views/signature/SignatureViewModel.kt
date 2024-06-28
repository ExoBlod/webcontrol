package com.webcontrol.angloamerican.ui.checklistpreuso.views.signature

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.SignatureUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignatureViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val signatureUseCase: SignatureUseCase
) : ViewModel(){

    private val _signatureWorker = MutableSharedFlow<SignatureUIEvent>()
    val signatureWorker get() = _signatureWorker.asSharedFlow()

    fun setSignatureWorker(signature: SignatureRequest) = launch {
        _signatureWorker.emit(SignatureUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                signatureUseCase.invoke(signature)
            }.onSuccess { responseSignature ->
                _signatureWorker.emit(SignatureUIEvent.Success(responseSignature))
            }.onFailure {
                _signatureWorker.emit(SignatureUIEvent.Error)
            }
        }
        _signatureWorker.emit(SignatureUIEvent.HideLoading)
    }
}