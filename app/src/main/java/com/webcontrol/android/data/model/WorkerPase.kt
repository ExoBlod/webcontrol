package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerPase(
        @SerializedName("WorkerId")
        var WorkerId: String? = null,

        @SerializedName("Reason")
        var Reason: String? = null,

        @SerializedName("Detail")
        var Detail: String? = null,

        @SerializedName("Date")
        var Date: String? = null,

        @SerializedName("Time")
        var Time: String? = null,

        @SerializedName("State")
        var State: String? = null,

        @SerializedName("Lote")
        var Lote: Int = 0
)