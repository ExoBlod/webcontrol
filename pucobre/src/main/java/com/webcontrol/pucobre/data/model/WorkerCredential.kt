package com.webcontrol.pucobre.data.model

import com.google.gson.annotations.SerializedName
import com.webcontrol.pucobre.data.model.VehicleList
import com.webcontrol.pucobre.data.model.WorkerCredentialPucobre

data class WorkerCredential (
    @SerializedName("credentialInfo")
    var workerCredentialPucobre: WorkerCredentialPucobre,
    @SerializedName("vehicleInfo")
    var vehicleList: ArrayList<VehicleList>
)