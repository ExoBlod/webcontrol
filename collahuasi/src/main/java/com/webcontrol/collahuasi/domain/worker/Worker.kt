package com.webcontrol.collahuasi.domain.worker

import com.google.gson.annotations.SerializedName

data class Worker(
        val workerId: String,

        val name: String,

        @SerializedName("lastname")
        val lastName: String,

        val ost: String,

        val passType: String,

        val costCenter: String,

        @SerializedName("lote")
        val batch: String,

        val companyId: String,

        val companyName: String,

        val divisionId: String,

        val isAuthorized: String,

        val isDriver: String,

        val email: String
) {
}