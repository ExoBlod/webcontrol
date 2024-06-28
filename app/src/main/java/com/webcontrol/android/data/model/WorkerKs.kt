package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerKs (
        @SerializedName("workerId")
        val workerId : String? = "",
        @SerializedName("name")
        val name: String? = "",
        @SerializedName("lastName")
        val lastName: String? = "",
        @SerializedName("rol")
        val rol: String? = "",
        @SerializedName("companyId")
        val companyId: String? = "",
        @SerializedName("companyName")
        val companyName: String? = "",
        @SerializedName("divisionId")
        val divisionId: String? = "",
        @SerializedName("autorizacion")
        val autorizacion: String? = "",
        @SerializedName("licencias")
        val licencias: String? = "NA",
        @SerializedName("photo")
        val photo: String? = "",
        @SerializedName("zonasAcceso")
        val zonasAcceso: String? = "",
        @SerializedName("fecExpLicencia")
        val fecExpLicencia: String? = "",
        @SerializedName("fecExpPsico")
        val fecExpPsico: String? = "",
        @SerializedName("fecIngreso")
        val fecIngreso: String? = "",
        @SerializedName("vehicleTypes")
        val vehicleTypes: String? = "",
        @SerializedName("zonasConduccion")
        val zonasConduccion: String? = "",
        @SerializedName("mandante")
        val mandante: String? = ""
)