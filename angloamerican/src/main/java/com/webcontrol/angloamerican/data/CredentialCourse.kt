package com.webcontrol.angloamerican.data

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.utils.Utils.getNiceDate
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable

data class CredentialCourse(
    @SerializedName("Codigo")
    val code: Int,
    @SerializedName("Curso")
    val course: Int,
    @SerializedName("Rut")
    val workerId: String,
    @SerializedName("Aprobado")
    val approved: String,
    @SerializedName("CursoName")
    val courseName: String,
    @SerializedName("FechaCurso")
    val date: String,
    @SerializedName("HoraCurso")
    val time: String,
    @SerializedName("Realizado")
    val done: String,
    @SerializedName("Finalizado")
    val finished: String,
    @SerializedName("Asistio")
    val attended: String,
    @SerializedName("Nota")
    val grade: Int,
    @SerializedName("FechaFin")
    val endDate: String,
    @SerializedName("HoraFin")
    val endTime: String,
) : Visitable<CredentialCourse> {
    override fun type(typeFactory: TypeFactory<CredentialCourse>): Int {
        return typeFactory.type(this)
    }

    @JvmName("fechaInicio")
    fun getDate(): String {
        return getNiceDate(date)
    }

    @JvmName("fechaFin")
    public fun getDateVigencia(): String {
        return getNiceDate(endDate)
    }
}