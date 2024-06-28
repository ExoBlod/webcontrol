package com.webcontrol.android.data.model.sgscm

import com.google.gson.annotations.SerializedName

data class AuthenticateRequest(
        @SerializedName("WorkerId")
        var workerId :String?=null,
        @SerializedName("TagNfc")
        var tagNfc:String?=null
) {
}