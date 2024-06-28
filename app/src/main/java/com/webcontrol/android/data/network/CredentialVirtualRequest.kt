package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class CredentialVirtualRequest(
    @SerializedName("WorkerId")
    val workerId: String
)