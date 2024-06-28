package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Ruta(
        @SerializedName("PATENTE")
        var vehicleI: String,

        @SerializedName("AUTORIZADO")
        var authorized: String,

        @SerializedName("FECHA")
        var date: String,

        @SerializedName("DIVISION")
        var divisionId: String,

        @SerializedName("IDSYNC")
        var idSync: Int,

        @SerializedName("ID_ASIG")
        var idAsig: Int,

        @SerializedName("CodigoTipoPermiso")
        var codigoTipoPermiso: Int
)