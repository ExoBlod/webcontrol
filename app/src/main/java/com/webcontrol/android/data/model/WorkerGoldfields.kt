package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

class WorkerGoldfields(
        @SerializedName("WorkerId")
        var id: String? = null,
        @SerializedName("Name")
        val nombre: String? = null,
        @SerializedName("LastName")
        val apellidos: String? = null,
        @SerializedName("Ost")
        val ost: String? = null,
        @SerializedName("PassType")
        val tipoPase: String? = null,
        @SerializedName("CostCenter")
        val centroCosto: String? = null,
        @SerializedName("CompanyId")
        val companiaId: String? = null,
        @SerializedName("CompanyName")
        val companiaNombre: String? = null,
        @SerializedName("IsAuthorized")
        val autor: Boolean?= false,
        @SerializedName("IsDriver")
        val driver: Boolean?= false,
        @SerializedName("CredentialEndDate")
        val credentialEndDate: String? = null,
        @SerializedName("WorkerCredencial")
        val credencial: String? = null,
        @SerializedName("DivisionId")
        val division: String? = null
) {

}
