package com.webcontrol.angloamerican.data

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.utils.StateSeatBus


class ResponseSeatBus(
    @SerializedName("ASIENTO")
    val idSeat: Int,
    @SerializedName("ESTADO")
    var stateSeat: String,
){
    override fun toString(): String {
        return "ResponseSeatBus(idSeat=$idSeat, stateSeat=$stateSeat)"
    }
}