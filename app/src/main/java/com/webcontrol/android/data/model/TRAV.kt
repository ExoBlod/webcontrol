package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class TRAV (
    @SerializedName("pais")
    var pais: String? = "",
    @SerializedName("fechaIn")
    var fechaIn: String? = "",
    @SerializedName("fechaOut")
    var fechaOut:String? = "",
    var checked:Boolean = false
)