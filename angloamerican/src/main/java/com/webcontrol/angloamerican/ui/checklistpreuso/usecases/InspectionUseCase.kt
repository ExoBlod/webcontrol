package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IInspectionRepository
import javax.inject.Inject

class InspectionUseCase @Inject constructor(private val repository: IInspectionRepository) {
    suspend operator fun invoke(request: InsertInspectionRequest) = repository.insertInspection(request)
}