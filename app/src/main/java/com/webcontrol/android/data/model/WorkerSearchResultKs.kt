package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerSearchResultKs (
    @SerializedName("workerId")
    var workerId : String = "",
    @SerializedName("name")
    var name : String = "",
    @SerializedName("lastname")
    var lastname: String = ""
)