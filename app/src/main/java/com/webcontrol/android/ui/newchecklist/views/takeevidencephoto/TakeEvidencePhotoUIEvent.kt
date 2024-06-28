package com.webcontrol.android.ui.newchecklist.views.takeevidencephoto

sealed class TakeEvidencePhotoUIEvent{
    data class Success(val responsePostEvidence: Boolean) : TakeEvidencePhotoUIEvent()
    object Error : TakeEvidencePhotoUIEvent()
    object ShowLoading : TakeEvidencePhotoUIEvent()
    object HideLoading : TakeEvidencePhotoUIEvent()
}
