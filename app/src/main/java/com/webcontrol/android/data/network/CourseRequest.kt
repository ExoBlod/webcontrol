package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class CourseRequest (
    @SerializedName("workerId")
    val workerId:String,
    @SerializedName("division")
    val divisionId:String
    ){
}