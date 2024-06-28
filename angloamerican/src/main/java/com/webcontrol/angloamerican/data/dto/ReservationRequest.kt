package com.webcontrol.angloamerican.data.dto

import com.google.gson.annotations.SerializedName

data class ReservationRequest(
    @SerializedName("WorkerMake")
    val idWorker: String,
    @SerializedName("CourseProg")
    val idCourseProg: Int,
    @SerializedName("DateReserve")
    val startDate: String,
    @SerializedName("ListWorker")
    val listWorker: List<String>
)