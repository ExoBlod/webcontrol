package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleInformationResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleRequest
import javax.inject.Inject

interface IVehicleRepository{
    suspend fun getVehicleInformation(plate: String): List<VehicleInformationResponse>
}

class VehicleRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): IVehicleRepository {
    override suspend fun getVehicleInformation(plate: String): List<VehicleInformationResponse>{
        val response = checkListPreUsoApi.getVehicleInformation(plate)
        val result = requireNotNull(response?.data)
        return result
    }
}