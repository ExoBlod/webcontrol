package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class BarrickResponse<T>(
    @SerializedName("Success" , alternate = ["success"]) val success: Boolean,
    @SerializedName("Message", alternate = ["message"]) val message: String,
    @SerializedName("Data", alternate = ["data"]) var data: T
) {

    override fun toString(): String {
        return ""
    }
}