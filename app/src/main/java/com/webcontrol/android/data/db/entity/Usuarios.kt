package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.webcontrol.android.util.ByteArrayToBase64TypeAdapter

@Entity(tableName = "usuarios", primaryKeys = ["RUT"])
class Usuarios {

    @NonNull
    @SerializedName("RUT")
    @ColumnInfo(name = "RUT")
    var rut: String = ""

    @SerializedName("NOMBRES")
    @ColumnInfo(name = "Nombres")
    var nombres: String? = ""

    @SerializedName("APELLIDOS")
    @ColumnInfo(name = "Apellidos")
    var apellidos: String? = ""

    @SerializedName("EMAIL")
    @ColumnInfo(name = "Email")
    var email: String? = ""

    @SerializedName("NUMCELULAR")
    @ColumnInfo(name = "NumCelular")
    var numCelular: String? = ""

    @SerializedName("ZIPCODE")
    @ColumnInfo(name = "ZipCode")
    var zipCode: String? = ""

    @SerializedName("ACTIVO")
    var Activo: String? = ""

    @SerializedName("FOTO")
    @JsonAdapter(ByteArrayToBase64TypeAdapter::class)
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB , name= "Foto")
    var foto: ByteArray ?=null

    @Ignore
    @SerializedName("EMPRESA")
    var Empresa: String? = null

    @Ignore
    @SerializedName("REQUIERECAMBIOPASS")
    var isRequiereCambioPass = false

}