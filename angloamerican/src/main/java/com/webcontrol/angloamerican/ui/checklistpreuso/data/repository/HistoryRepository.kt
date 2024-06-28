package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerRequest
import javax.inject.Inject

interface IHistoryRepository{
    suspend fun getHistory(request: WorkerRequest): List<HistoryResponse>
    suspend fun getHistoryById(request: HistoryByWorkerIdRequest): List<HistoryResponse>
    suspend fun getHistoryByPlate(request: HistoryByPlateRequest): List<HistoryResponse>
}

class HistoryRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): IHistoryRepository {
    override suspend fun getHistory(request: WorkerRequest): List<HistoryResponse>{
        val response = checkListPreUsoApi.getHistory(request.WorkerId)
        val result = requireNotNull(response?.data)
        return result
    }

    override suspend fun getHistoryById(request: HistoryByWorkerIdRequest): List<HistoryResponse>{
        val response = checkListPreUsoApi.getHistoryByWorkerId(request.workerId,request.dateQuery)
        val result = requireNotNull(response?.data)
        return result
    }

    override suspend fun getHistoryByPlate(request: HistoryByPlateRequest): List<HistoryResponse>{
        val response = checkListPreUsoApi.getHistoryByPlate(request.plate,request.dateQuery)
        val result = requireNotNull(response?.data)
        return result
    }
}