package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureResponse
import javax.inject.Inject

interface ISignatureRepository{
    suspend fun setSignature(request: SignatureRequest): List<SignatureResponse>
}

class SignatureRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): ISignatureRepository {
    override suspend fun setSignature(request: SignatureRequest): List<SignatureResponse>{
        val response = checkListPreUsoApi.setSignature(request)
        val result = requireNotNull(response?.data)
        return result
    }
}