package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerEA (
        var rut : String = "",
        val apellidos: String = "",
        val nombres: String = "",
        val rol: String = "",
        val empresaId: String = "",
        val empresa: String = "",
        val divisionId: String = "",
        var foto: String = "",
        val autorizado: String = "",
        var clase: String?= "NA",
        @SerializedName("autCourse")
        val autCurso: String = "",
        @SerializedName("zonas_conduce")
        val zonasConduce: String = "",
        @SerializedName("tipo_veh")
        val tipoVehiculos: String = "",
        @SerializedName("fec_exp_psico")
        val fecExpPsico: String = "",
        @SerializedName("cargoElectrico")
        val cargoElectrico: String = "",
        @SerializedName("validadoDocCargo")
        val documentoCargoElectrico: String = ""
){
}