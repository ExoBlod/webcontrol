package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.model.ParkingUsage
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class InsertPreaccessMineUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(preaccessMine: PreaccesoMina) = repository.insertPreaccessMine(preaccessMine)
}