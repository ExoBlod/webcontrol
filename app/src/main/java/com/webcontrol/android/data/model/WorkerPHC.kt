package com.webcontrol.android.data.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WorkerPHC : Serializable {
    @SerializedName("workerId")
    @PrimaryKey
    var id: String = ""

    @SerializedName("name")
    var nombre: String = ""

    @SerializedName("lastName")
    var apellidos: String? = ""

    @SerializedName("ost")
    var ost: String? = ""

    @SerializedName("passType")
    var tipoPase: String? = ""

    @SerializedName("costCenter")
    var centroCosto: String? = ""

    @SerializedName("companyId")
    var companiaId: String = ""

    @SerializedName("companyName")
    var companiaNombre: String? = ""

    @SerializedName("divisionId")
    var division: String = ""

    @SerializedName("isAuthorized")
    var autor: String = ""

    @SerializedName("isDriver")
    var driver: String = ""

    @SerializedName("credentialEndDate")
    var credentialEndDate: String? = ""

    @SerializedName("credencialVirtual")
    var credencial: String? = ""

    @SerializedName("cargo")
    var cargo: String? = ""
}