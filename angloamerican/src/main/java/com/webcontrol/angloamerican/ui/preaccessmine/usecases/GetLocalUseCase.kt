package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.model.LocalRequest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class GetLocalUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(localRequest: LocalRequest) = repository.getLocal(localRequest)
}