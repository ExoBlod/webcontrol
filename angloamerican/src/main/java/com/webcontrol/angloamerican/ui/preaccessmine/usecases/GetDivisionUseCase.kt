package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class GetDivisionUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke() = repository.getDivision()
}