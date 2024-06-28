package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IVehicleRepository
import javax.inject.Inject

class VehicleInformationUseCase @Inject constructor(private val repository: IVehicleRepository){
    suspend operator fun invoke(plate: String) = repository.getVehicleInformation(plate)
}