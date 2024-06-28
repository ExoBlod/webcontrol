package com.webcontrol.angloamerican.ui.checklistpreuso.views.takeevidencephoto

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class TakeEvidencePhotoViewModel @Inject
constructor(
    @ApplicationContext val context: Context
) : ViewModel(){
    
    private val _photoEvidence = MutableSharedFlow<TakeEvidencePhotoUIEvent>()
    val photoEvidence get() = _photoEvidence.asSharedFlow()

    var numEvidenceSend = 0

}