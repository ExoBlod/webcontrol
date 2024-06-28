package com.webcontrol.android.data.model.sgscm

import com.google.gson.annotations.SerializedName

data class WorkerSgscmResponse (
        val workerId:String?,
        @SerializedName("name")
        val nameWorker:String?,
        @SerializedName("lastName")
        val lastNameWorker:String?,
        val ost:String?,
        val passType:String?,
        val costCenter:String?,
        val companyId:String?,
        val companyName:String?,
        val turn:String?,
        val divisionId:String?,
        val lote:Int?,
        val isAuthorized:Boolean?,
        val isDriver:Boolean?,
        val isValidated:Boolean?,
        @SerializedName("startDateOfPass")
        val startDatePass:String?,
        @SerializedName("endDateOfPass")
        val endDatePass:String?,
        val syncId:Long?,
        val workerCredencial:String?,
        val rol:String?,
        val clase:String?,
        val autorizacion:String?


) {

}