package com.webcontrol.android.data.model

import androidx.annotation.NonNull
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WorkerAnglo : Serializable {
    @SerializedName("WorkerId")
    @PrimaryKey
    @NonNull
    var id: String = ""

    @SerializedName("Name")
    var nombre: String = ""

    @SerializedName("LastName")
    var apellidos: String? = ""

    @SerializedName("Ost")
    var ost: String? = ""

    @SerializedName("PassType")
    var tipoPase: String? = ""

    @SerializedName("CostCenter")
    var centroCosto: String? = ""

    @SerializedName("CompanyId")
    var companiaId: String = ""

    @SerializedName("CompanyName")
    var companiaNombre: String? = ""

    @SerializedName("IsAuthorized")
    var autor: Boolean = false

    @SerializedName("IsDriver")
    var driver: Boolean = false

    @SerializedName("CredentialEndDate")
    var credentialEndDate: String? = ""

    @SerializedName("WorkerCredencial")
    var credencial: String? = ""

    @SerializedName("DivisionId")
    var division: String = ""

    @SerializedName("StateCertification")
    var stateCertification: String = ""

    @SerializedName("IsValidated")
    var validated: Boolean = false

    @SerializedName("IsMandante")
    var isMandante: Boolean = false

    @SerializedName("FilterRC")
    var filterRC: Int = 0

    @SerializedName("Licencias")
    var licencias: String? = ""

    @SerializedName("Cargo")
    var cargo: String? = ""

    var IsAprrovedMov: String? = ""

    val isApproverUser: Boolean
        get() = IsAprrovedMov == IS_APPROVED

    constructor() {}
    constructor(
        nombre: String,
        apellidos: String?,
        ost: String?,
        tipoPase: String?,
        centroCosto: String?,
        companiaId: String,
        companiaNombre: String?,
        autor: Boolean,
        driver: Boolean,
        credentialEndDate: String?,
        credencial: String?,
        division: String,
        stateCertification: String,
        validated: Boolean,
        licencias: String?,
        cargo: String?
    ) {
        this.nombre = nombre
        this.apellidos = apellidos
        this.ost = ost
        this.tipoPase = tipoPase
        this.centroCosto = centroCosto
        this.companiaId = companiaId
        this.companiaNombre = companiaNombre
        this.autor = autor
        this.driver = driver
        this.credentialEndDate = credentialEndDate
        this.credencial = credencial
        this.division = division
        this.stateCertification = stateCertification
        this.validated = validated
        this.licencias = licencias
        this.cargo = cargo
    }
}

const val IS_APPROVED = "1"