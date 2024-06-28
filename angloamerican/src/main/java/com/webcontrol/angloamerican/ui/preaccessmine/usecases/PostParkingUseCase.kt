package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.model.ParkingUsage
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class PostParkingUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(parkingUsage: ParkingUsage) = repository.postParking(parkingUsage)
}