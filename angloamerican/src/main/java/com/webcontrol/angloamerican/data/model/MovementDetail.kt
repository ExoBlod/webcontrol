package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class MovementDetail(
    @SerializedName("LOTENUM")
    val batchNumber: Int,

    @SerializedName("RUTLOTE")
    val batchRUT: String,

    @SerializedName("FECHACREACION")
    val creationDate: String,

    @SerializedName("LOTEESTADO")
    val batchState: String,

    @SerializedName("LOTEFINICIO")
    val startDate: String,

    @SerializedName("LOTEFFINAL")
    val endDate: String,

    @SerializedName("USUARIOAPROBADOR")
    val approverUserId: String,

    @SerializedName("NAMEUSUARIOAPROBADOR")
    val approverUserName: String,

    @SerializedName("IDSPONSOR")
    val sponsorId: String?,

    @SerializedName("NAMESPONSOR")
    val sponsorName: String?,

    @SerializedName("EMAILSPONSOR")
    val sponsorEmail: String?,

    @SerializedName("TELEFONOSPONSOR")
    val sponsorPhone: String?,

    @SerializedName("IDEMPRESALT")
    val alternateCompanyId: String?,

    @SerializedName("NAMEEMPRESLT")
    val alternateCompanyName: String?,

    @SerializedName("TIPOPASE")
    val passType: String,

    @SerializedName("OSTLT")
    val ostlt: String,

    @SerializedName("IDDIVISION")
    val divisionId: String,

    @SerializedName("DIVISION")
    val division: String
)
