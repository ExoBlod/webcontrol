package com.webcontrol.android.data.network.dto

import com.google.gson.annotations.SerializedName

data class SyncEstadoDto(
        @SerializedName("Id")
        var id: Int,

        @SerializedName("Estado")
        var estado: Int,

        @SerializedName("FechaModificacion")
        private val fechaModificacion: String,

        @SerializedName("Importante")
        private val isImportant: String
)