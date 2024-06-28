package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionResponse
import javax.inject.Inject

interface IInspectionRepository{
    suspend fun insertInspection(request: InsertInspectionRequest): List<InsertInspectionResponse>
}

class InspectionRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): IInspectionRepository {
    override suspend fun insertInspection(request: InsertInspectionRequest): List<InsertInspectionResponse>{
        val response = checkListPreUsoApi.insertInspection(request)
        val result = requireNotNull(response?.data)
        return result
    }
}