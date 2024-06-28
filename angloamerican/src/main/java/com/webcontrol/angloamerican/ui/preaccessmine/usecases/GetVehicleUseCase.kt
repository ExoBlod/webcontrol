package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.model.LocalRequest
import com.webcontrol.angloamerican.data.model.VehicleRequest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class GetVehicleUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(vehicleRequest: VehicleRequest) = repository.getVehicle(vehicleRequest)
}