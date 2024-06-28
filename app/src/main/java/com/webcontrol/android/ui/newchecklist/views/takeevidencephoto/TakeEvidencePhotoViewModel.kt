package com.webcontrol.android.ui.newchecklist.views.takeevidencephoto

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.WorkerSignature
import com.webcontrol.android.util.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TakeEvidencePhotoViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
   private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){

    init {
    }
    
    private val _photoEvidence = MutableSharedFlow<TakeEvidencePhotoUIEvent>()
    val photoEvidence get() = _photoEvidence.asSharedFlow()

    var numEvidenceSend = 0

    fun sendPhotoEvidence(newCheckListGroup: NewCheckListGroup) = launch {
        _photoEvidence.emit(TakeEvidencePhotoUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.postPhotoEvidence(newCheckListGroup)
            }.onSuccess { responseSignature ->
                _photoEvidence.emit(TakeEvidencePhotoUIEvent.Success(responseSignature))
            }.onFailure {
                _photoEvidence.emit(TakeEvidencePhotoUIEvent.Error)
            }
        }
        _photoEvidence.emit(TakeEvidencePhotoUIEvent.HideLoading)
    }
}