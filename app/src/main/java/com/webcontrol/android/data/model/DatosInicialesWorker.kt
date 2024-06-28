package com.webcontrol.android.data.model

import androidx.room.Ignore
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class DatosInicialesWorker(
        @SerializedName("Rut")
        var rut: String,
        @SerializedName("Nombre")
        val nombre: String? = null,
        @SerializedName("Apellido")
        val apellidos: String? = null,
        @SerializedName("FechNac")
        var fechaNacimiento: String? = null,
        @SerializedName("Sexo")
        val sexo: String? = null,
        @SerializedName("Nacionalidad")
        var nacionalidad: String? = null,
        @SerializedName("Email")
        var email: String? = null,

        @SerializedName("Empresa")
        var companiaId: String? = null,
        @SerializedName("Ocupacion")
        var ocupacion: String? = null,
        @SerializedName("TipoSeguro")
        var tipoSeguro: String? = null,
        @SerializedName("IngresoTipoSeguro")
        var ingresoSeguro: String? = null,

        @SerializedName("Celular")
        var celular: String? = null,
        @SerializedName("Telefono")
        var telefono: String? = null,
        @SerializedName("ContactoFamiliar")
        var contactoFamiliar: String? = null,
        @SerializedName("CelularContacto")
        var contactoCelular: String? = null,
        @SerializedName("Direccion")
        var direccion: String? = null,
        @SerializedName("DistRes")
        var distrito: String? = null,
        @SerializedName("ProvRes")
        var provincia: String? = null,
        @SerializedName("DepartamentoRes")
        var departamento: String? = null,
        @SerializedName("CodDep")
        var departamentoId: String? = null,
        @SerializedName("Pais")
        var pais: String? = null,
        @SerializedName("TipoRes")
        var tipoResidencia: String? = null,
        @SerializedName("TipoDoc")
        var tipoDoc: String?= null,
        @SerializedName("AntecedentesMedicos")
        val listAntecedentes: List<Antecedentes>
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}