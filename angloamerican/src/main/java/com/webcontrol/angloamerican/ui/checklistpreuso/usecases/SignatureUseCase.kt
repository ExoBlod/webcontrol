package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.ISignatureRepository
import javax.inject.Inject

class SignatureUseCase @Inject constructor(private val repository: ISignatureRepository) {
    suspend operator fun invoke(request: SignatureRequest) = repository.setSignature(request)
}