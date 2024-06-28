package com.webcontrol.android.ui.newchecklist.views.signature

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.ui.newchecklist.data.WorkerSignature
import com.webcontrol.android.ui.newchecklist.views.listchecklist.ListChecklistUIEvent
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.launch
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
   private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){

    private val _signatureWorker = MutableSharedFlow<SignatureUIEvent>()
    val signatureWorker get() = _signatureWorker.asSharedFlow()

    fun setSignatureWorker(signature: WorkerSignature) = launch {
        _signatureWorker.emit(SignatureUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.setSignatureWorker(signature)
            }.onSuccess { responseSignature ->
                _signatureWorker.emit(SignatureUIEvent.Success(responseSignature))
            }.onFailure {
                _signatureWorker.emit(SignatureUIEvent.Error)
            }
        }
        _signatureWorker.emit(SignatureUIEvent.HideLoading)
    }
    init {
    }
}