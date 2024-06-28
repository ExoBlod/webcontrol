package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class ApproverUserResponse(
    @SerializedName("UserApprover")
    val userApprover: Int? = 0
){
    fun isApproverUser(): Boolean{
        if(userApprover == 0){
            return false
        }
        return true
    }
}