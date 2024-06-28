package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerSearchResult (
    @SerializedName("id")
    var id : Long = 0,
    @SerializedName("personId")
    var personId : Long = 0,
    @SerializedName("workerId")
    var workerId: String = ""
)