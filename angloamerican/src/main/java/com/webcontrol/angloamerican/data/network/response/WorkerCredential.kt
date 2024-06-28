package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.CREDENTIAL_BACK
import com.webcontrol.angloamerican.data.CREDENTIAL_BACK_LIST_VEHICLE
import com.webcontrol.angloamerican.data.CREDENTIAL_FRONT
import com.webcontrol.angloamerican.data.CREDENTIAL_LIST_COURSES

data class WorkerCredential(
    @SerializedName(CREDENTIAL_FRONT)
    var credentialFront: CredentialFront,
    @SerializedName(CREDENTIAL_BACK)
    var credentialBack: ArrayList<CredentialBack>,
    @SerializedName(CREDENTIAL_BACK_LIST_VEHICLE)
    var credentialBackVehicle: ArrayList<CredentialBackVehicle>,
    @SerializedName(CREDENTIAL_LIST_COURSES)
    var credentialListCourses: ArrayList<CredentialListCourses>
)