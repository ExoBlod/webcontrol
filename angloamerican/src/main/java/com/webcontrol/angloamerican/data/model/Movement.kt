package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class Movement(
    @SerializedName("IDLOTE")
    val batchId: Int,

    @SerializedName("LOTEESTADO")
    val batchState: String,

    @SerializedName("USUARIOAPROBADOR")
    val approverUser: String,

    @SerializedName("RUTLOTE")
    val batchRUT: String,

    @SerializedName("TIPORUT")
    val rutType: String,

    @SerializedName("RUTPROPIETARIO")
    val ownerRUT: String,

    @SerializedName("NOMBREPROPIETARIO")
    val ownerName: String,

    @SerializedName("DIVISION")
    val division: String,

    @SerializedName("LOTEFINICIO")
    val initialBatch: String,

    @SerializedName("LOTEFFINAL")
    val finalBatch: String,

    @SerializedName("OSTLT")
    val ostlt: String,

    @SerializedName("DESCRIPCION")
    val description: String
)