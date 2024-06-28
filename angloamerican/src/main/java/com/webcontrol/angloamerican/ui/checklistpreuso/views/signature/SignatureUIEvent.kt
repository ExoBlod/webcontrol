package com.webcontrol.angloamerican.ui.checklistpreuso.views.signature

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureResponse

sealed class SignatureUIEvent {
    data class Success(val signatureResponse: List<SignatureResponse>) : SignatureUIEvent()
    object Error : SignatureUIEvent()
    object ShowLoading : SignatureUIEvent()
    object HideLoading : SignatureUIEvent()
}
