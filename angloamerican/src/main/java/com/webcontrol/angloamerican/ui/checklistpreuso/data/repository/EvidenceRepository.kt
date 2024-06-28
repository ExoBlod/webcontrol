package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.UploadEvidenceRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.UploadEvidenceResponse

class EvidenceRepository(
    private val checkListPreUsoApi: CheckListPreUsoApi
) {
    suspend fun uploadEvidence(request: UploadEvidenceRequest): List<UploadEvidenceResponse>{
        val response = checkListPreUsoApi.uploadEvidence(request)
        val result = requireNotNull(response?.data)
        return result
    }
}