package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class PostReservationPreaccessMineUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke() : Boolean {
        val pending = repository.getPreAccessMineAll().filter { it.estado == "P" }
        val response = repository.postReservationPreaccessMine(pending)
        if(response.isNotEmpty()){
            pending.forEach { preaccesoMina ->
                repository.updatePreaccessStatus(preaccesoMina.id)
            }
            return true
        }
        return false
    }
}