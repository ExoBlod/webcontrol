package com.webcontrol.android.data.network.dto

import com.google.gson.annotations.SerializedName

class CredentialVirtualDocuments(
    @SerializedName("NOMBRE_DOCUMENTO")
    val NOMBRE_DOCUMENTO: String,

    @SerializedName("FECHA_VIGENCIA")
    val FECHA_VIGENCIA: String
)