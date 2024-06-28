package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkerAnta(
    @SerializedName("rut")
    var rut: String = "",
    @SerializedName("nombres")
    val nombres: String = "",
    @SerializedName("apellidos")
    val apellidos: String = "",
    @SerializedName("fec_nacimiento")
    val fechaNacimiento: String = "",
    @SerializedName("ccosto")
    val ccosto: String = "",
    @SerializedName("ost")
    val ost: String = "",
    @SerializedName("empresa")
    val empresa: String = "",
    @SerializedName("autor")
    val autor: String = "",
    @SerializedName("finipase")
    val finipase: String = "",
    @SerializedName("ffinpase")
    val ffinpase: String = ""
): Parcelable