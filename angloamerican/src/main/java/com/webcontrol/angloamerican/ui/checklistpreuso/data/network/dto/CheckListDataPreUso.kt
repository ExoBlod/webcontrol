package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class CheckListDataPreUso(
    @SerializedName("ID_TIPO")
    var idTipo: String,

    @SerializedName("ID_CHECK")
    var idCheck: Int,

    @SerializedName("ID_CHECKGROUP")
    var idCheckGroup: Int,

    @SerializedName("NOMBRECHECKGROUP")
    var nombreCheckGroup: String,

    @SerializedName("ID_CHECKDET")
    var idCheckDet: Int,

    @SerializedName("NOMBRE")
    var nombre: String,

    @SerializedName("DESCRIPCION")
    var descripcion: String,

    @SerializedName("REQFOTO")
    var reqFoto: String,

    @SerializedName("REQCODBARRA")
    var reqCodigoBarra: String,

    @SerializedName("REQDOCUMENTO")
    var reqDocumento: String,

    @SerializedName("ORDEN")
    var orden: Int,

    @SerializedName("IDSYNC")
    var idSync: Int,

    @SerializedName("USRCREA")
    var usrCrea: String,

    @SerializedName("FECCREA")
    var fecCrea: String,

    @SerializedName("USRMOD")
    var usrMod: String,

    @SerializedName("FECMOD")
    var fecMod: String,

    @SerializedName("REQVIDEO")
    var reqVideo: String,

    @SerializedName("TIPO")
    var tipo: String,

    @SerializedName("VALOR")
    var valor: String,

    @SerializedName("VALORMULT")
    var valorMult: String,

    @SerializedName("CRITICO")
    var critico: Boolean,
    
    @SerializedName("FOTO")
    var foto: String
)
