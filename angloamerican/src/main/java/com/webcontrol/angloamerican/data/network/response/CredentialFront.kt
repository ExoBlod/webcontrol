package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.*

class CredentialFront(
    @SerializedName(DATE_LIC_DRIVER)
    var dateDriverLic: String,
    @SerializedName(DATE_PSICOSENSO_TEC)
    var dateTecPsicosenso: String,
    @SerializedName(DATE_IN)
    var dateIn: String,
    @SerializedName(MUNICIPAL)
    var municipal: String,
    @SerializedName(PHOTO)
    var photo: String?,
    @SerializedName(ROL)
    var rol: String,
    @SerializedName(AUTHORIZED)
    var autorizathe: String,
    @SerializedName(PRINCIPAL)
    var principal: String,
    @SerializedName(RUT)
    var rut: String,
    @SerializedName(LAST_NAME)
    var lastName: String,
    @SerializedName(FIRST_NAME)
    var firstName: String,
    @SerializedName(COMPANY)
    var company: String,
    @SerializedName(AREA)
    var area: String,
    @SerializedName(ACCESS_ZONE)
    var accessZones: String,
    @SerializedName(DIVISION)
    var div: String
)