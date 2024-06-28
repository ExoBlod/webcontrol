package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName
import com.webcontrol.android.data.model.WorkerCredentialBarrick

data class ApiResponsBarrickCredential(

    @SerializedName("PAIS")
    var pais: String,

    @SerializedName("CREDENCIAL")
    var credencial: WorkerCredentialBarrick

)