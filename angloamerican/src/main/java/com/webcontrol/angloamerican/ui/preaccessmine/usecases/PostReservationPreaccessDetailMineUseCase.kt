package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class PostReservationPreaccessDetailMineUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke() : Boolean {
        val pending = repository.getPreAccessDetailMineAll().filter { it.estado == "P" }
        val response = repository.postReservationPreaccessDetailMine(pending)
        if(response.isNotEmpty()){
            pending.forEach { preaccesoDetallemina ->
                repository.updatePreaccessDetailStatus(preaccesoDetallemina.id)
            }
            return true
        }
        return false
    }
}