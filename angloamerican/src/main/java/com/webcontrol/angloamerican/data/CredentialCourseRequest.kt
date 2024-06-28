package com.webcontrol.angloamerican.data

import com.google.gson.annotations.SerializedName

data class CredentialCourseRequest(
    @SerializedName("Rut")
    val workerId: String,
    @SerializedName("PageSize")
    val pageSize: Int
)