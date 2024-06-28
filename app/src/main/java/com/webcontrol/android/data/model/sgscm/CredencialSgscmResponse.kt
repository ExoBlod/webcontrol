package com.webcontrol.android.data.model.sgscm

import com.google.gson.annotations.SerializedName

data class CredencialSgscmResponse(
        val workerId:String?,
        @SerializedName("name")
        val nameWorker:String?,
        @SerializedName("lastName")
        val lastNameWorker:String?,
        val rol:String?,
        val companyId:String?,
        val companyName:String?,
        @SerializedName("zonas_conduce")
        val zonaConduce:String?,
        @SerializedName("equipos_veh")
        val equiposVehiculo:String?,
        @SerializedName("categoria_veh")
        val categoriaVehiculo:String?,
        val clase:String?,
        val autorizacion:String?,
        val mandante:String?,
        val foto:String?
){
}