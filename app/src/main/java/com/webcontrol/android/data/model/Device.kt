package com.webcontrol.android.data.model

data class Device(
        var rut: String,
        private val imei: String,
        private val token: String
)