package com.webcontrol.android.ui.newchecklist.views.signature

import com.webcontrol.android.ui.newchecklist.data.WorkerSignature

sealed class SignatureUIEvent {
    data class Success(val workerSignature: Boolean) : SignatureUIEvent()
    object Error : SignatureUIEvent()
    object ShowLoading : SignatureUIEvent()
    object HideLoading : SignatureUIEvent()
}
