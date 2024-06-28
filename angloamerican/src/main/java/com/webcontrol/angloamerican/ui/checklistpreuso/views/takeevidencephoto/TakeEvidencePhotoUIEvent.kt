package com.webcontrol.angloamerican.ui.checklistpreuso.views.takeevidencephoto

sealed class TakeEvidencePhotoUIEvent{
    data class Success(val responsePostEvidence: Boolean) : TakeEvidencePhotoUIEvent()
    object Error : TakeEvidencePhotoUIEvent()
    object ShowLoading : TakeEvidencePhotoUIEvent()
    object HideLoading : TakeEvidencePhotoUIEvent()
}
