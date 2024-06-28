package com.webcontrol.collahuasi.domain.attendance

data class Attendance(
        val registerId: String,
        val deviceId: String,
        val rut: String,
        val date: String,
        val time: String,
        val inOut: String,
        val correlative: String,
        val indicted: String,
        val reason: String,
        val lat: String,
        val long: String
)