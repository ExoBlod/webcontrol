package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
data class CourseData(
    @SerializedName("IdProgram")
    val idProgram: Int,
    @SerializedName("CharlaName")
    val charlaName: String,
    @SerializedName("IdCourse")
    val idCourse: Int,
    @SerializedName("IdExamen")
    val idExamen: Int,
    @SerializedName("StartDate")
    val startDate: String,
    @SerializedName("StartHour")
    val startHour: String,
    @SerializedName("EndDate")
    val endDate: String,
    @SerializedName("EndHour")
    val endHour: String,
    @SerializedName("Place")
    val place: String,
    @SerializedName("Room")
    val room: String,
    @SerializedName("CourseType")
    val courseType: String,
    @SerializedName("Duracion")
    val duration: String,
    @SerializedName("Capacidad")
    val capacity: String,
    @SerializedName("Cantidad")
    val amount: String,
)
