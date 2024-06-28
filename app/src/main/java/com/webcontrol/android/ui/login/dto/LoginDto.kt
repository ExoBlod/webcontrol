package com.webcontrol.android.ui.login.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
        @SerializedName("USERNAME")
        private val username: String,

        @SerializedName("PASSWORD")
        private val password: String
)