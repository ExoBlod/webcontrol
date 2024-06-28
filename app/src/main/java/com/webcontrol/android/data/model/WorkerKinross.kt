package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerKinross (
        @SerializedName("WorkerId")
        var workerId : String = "",
        @SerializedName("Name")
        val name: String = "",
        @SerializedName("LastName")
        val lastname: String = "",
        @SerializedName("Ost")
        val ost: String = "",
        @SerializedName("PassType")
        val passtype: String = "",
        @SerializedName("CostCenter")
        val costcenter: String = "",
        @SerializedName("CompanyId")
        val companyid: String? = "",
        @SerializedName("CompanyAcronym")
        var companyacronym: String?= "NA",
        @SerializedName("CompanyName")
        val companyname: String? = "",
        @SerializedName("DivisionId")
        val divisionid: String? = "",
        @SerializedName("IsAuthorized")
        val isauthorized: Boolean = false,
        @SerializedName("IsDriver")
        val isdriver: Boolean = false,
        @SerializedName("Turn")
        val turn: String? = "",
        @SerializedName("Ports")
        val ports: Int = 0,
        @SerializedName("CredentialEndDate")
        var credentialenddate: String? = "",
        @SerializedName("WorkerCredencial")
        var workercredential: String? = ""
) {
}