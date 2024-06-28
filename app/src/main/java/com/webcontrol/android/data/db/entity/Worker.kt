package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.webcontrol.android.util.ByteArrayToBase64TypeAdapter
import java.io.Serializable

@Entity(tableName = "worker")
class Worker : Serializable {
    @SerializedName("RUT")
    @PrimaryKey
    @NonNull
    var rut: String = ""

    @SerializedName("NOMBRES")
    var nombres: String? = ""

    @SerializedName("APELLIDOS")
    var apellidos: String? = ""

    @SerializedName("ACTIVO")
    var activo: String? = ""

    @SerializedName("EMAIL")
    @ColumnInfo(name = "email_address")
    var email: String? = ""

    @SerializedName("FOTO")
    @JsonAdapter(ByteArrayToBase64TypeAdapter::class)
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    lateinit var foto: ByteArray

    @Ignore
    var password: String? = null

    @Ignore
    var newPassword: String? = null

    @Ignore
    @SerializedName("NUMCELULAR")
    var celular: String? = null

    @Ignore
    @SerializedName("ZIPCODE")
    var zipcode: String? = null

    @Ignore
    var codVerificacion: String? = null

    @Ignore
    @SerializedName("REQUIERECAMBIOPASS")
    var changePassRequired = false

    @Ignore
    constructor(rut: String, nombres: String?, apellidos: String?, autor: String?) {
        this.rut = rut
        this.nombres = nombres
        this.apellidos = apellidos
        this.activo = autor
    }

    constructor() {}

    @Ignore
    private constructor(params: Builder) {
        this.rut = params.rut
        this.nombres = params.nombres
        this.apellidos = params.apellidos
        this.activo = params.activo
        this.email = params.emailAddress
        this.foto = params.foto
        this.celular = params.celular
        this.zipcode = params.zipcode
    }

    class Builder(
        var rut: String,
        var nombres: String?,
        var apellidos: String?,
        var activo: String? ,
        var emailAddress: String,
        var foto: ByteArray,
        var pass: String?,
        var celular: String?,
        var zipcode: String?
    ) {
        init {
            this.rut = rut
            this.nombres = nombres
            this.apellidos = apellidos
        }

        fun foto(foto: ByteArray): Builder {
            this.foto = foto
            return this
        }

        fun build(): Worker {
            return Worker(this)
        }
    }
}