package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IHistoryRepository
import javax.inject.Inject

class GetHistoryPreUsoUseCase @Inject constructor(private val repository: IHistoryRepository) {
    suspend operator fun invoke(workerId: String) = repository.getHistory(request = WorkerRequest(workerId))
}