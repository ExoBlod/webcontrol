package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.*

class CredentialBack (
    @SerializedName(OPERATION)
    var operation : String = "",
    @SerializedName(ACTIVE)
    var active : String = "",
    @SerializedName(DELIVERY_DATE)
    var deliveryDate : String = "",
    @SerializedName(EXP_DATE)
    var expirationDate : String = "",
    @SerializedName(RESTRICTION)
    var retrictions : String = "",
)