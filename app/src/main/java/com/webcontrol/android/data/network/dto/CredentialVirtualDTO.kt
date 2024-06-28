package com.webcontrol.android.data.network.dto

import com.google.gson.annotations.SerializedName
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable

data class CredentialVirtualDTO(
    @SerializedName("credentialFront")
    val credentialFront: CredentialVirtualFront,
    @SerializedName("credentialBack")
    val credentialBack: CredentialVirtualBack,
    )
