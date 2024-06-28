package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.model.ParkingUsage
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class InsertPreaccessDetailMineUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(preaccesoDetalleMina: PreaccesoDetalleMina) = repository.insertPreAccessDetailMine(preaccesoDetalleMina)
}