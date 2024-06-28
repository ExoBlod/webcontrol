package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerCredentialPHC(
    @SerializedName("flicconducir") var workerFlicconducir: String = "",
    @SerializedName("fpsicosensotecnico") var workerPsico: String = "",
    @SerializedName("fingreso") val fechain: String = "",
    @SerializedName("foto") var foto: String = "",
    @SerializedName("rol") val rol: String = "",
    @SerializedName("autorizado") var workerAutorizado: String = "",
    @SerializedName("rut") var workerId: String = "",
    @SerializedName("apellidos") var workerApellidos: String = "",
    @SerializedName("nombres") var workerNombres: String = "",
    @SerializedName("empresa") var workerEmpresa: String = "",
    @SerializedName("municipal") var WorkerMunicipal: String = "",
)
