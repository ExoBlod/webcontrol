package com.webcontrol.android.data.db.entity

import com.webcontrol.android.util.ByteArrayToBase64TypeAdapter
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.webcontrol.android.data.db.entity.WorkerUnregistered
import java.io.Serializable

class WorkerUnregistered : Serializable {
    var id = 0

    @SerializedName("RUT")
    var rut: String? = ""

    @SerializedName("NOMBRES")
    var nombres: String? = ""

    @SerializedName("APELLIDOS")
    var apellidos: String? = ""

    @SerializedName("ACTIVO")
    var activo: String? = ""

    @SerializedName("EMAIL")
    var emailAddress: String? = ""

    @Ignore
    @SerializedName("NUMCELULAR")
    var celular: String? = ""

    @Ignore
    @SerializedName("ZIPCODE")
    var zipcode: String? = ""

    @SerializedName("FOTO")
    @JsonAdapter(ByteArrayToBase64TypeAdapter::class)
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var foto: ByteArray? = null

    @SerializedName("PASS")
    var pass: String? = ""

    @Ignore
    var codVerificacion: String? = ""

    constructor() {}

    @Ignore
    private constructor(params: Builder) {
        this.rut = params.rut
        this.nombres = params.nombres
        this.apellidos = params.apellidos
        this.activo = params.activo
        this.emailAddress = params.emailAddress
        this.foto = params.foto
        this.pass = params.pass
        this.celular = params.celular
        this.zipcode = params.zipcode
    }

    class Builder (
        var rut: String,
        var nombres: String,
        var apellidos: String,
        var activo: String? ,
        var emailAddress: String?,
        var foto: ByteArray,
        var pass: String? ,
        var celular: String? ,
        var zipcode: String?
    ){
        init {
            this.rut = rut
            this.nombres = nombres
            this.apellidos = apellidos
        }

        fun foto(foto: ByteArray): Builder {
            this.foto = foto
            return this
        }

        fun build(): WorkerUnregistered {
            return WorkerUnregistered(this)
        }
    }
}