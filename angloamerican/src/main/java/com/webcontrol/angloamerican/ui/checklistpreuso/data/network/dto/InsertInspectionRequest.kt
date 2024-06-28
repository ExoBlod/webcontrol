package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

data class InsertInspectionRequest (
    var workerId: String,
    var plate: String,
    var model: String,
    var brand: String,
    var turn: String,
    var odometer: Int,
    var typeVehicle: String,
    var maintenance: Int,
    var fuelLevel: String
)

