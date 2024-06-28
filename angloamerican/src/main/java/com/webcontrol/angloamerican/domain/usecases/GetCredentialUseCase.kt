package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.ICredentialRepository
import javax.inject.Inject

class GetCredentialUseCase @Inject constructor(private val repository: ICredentialRepository) {
    suspend operator fun invoke(workerId: String) = repository.getCredential(workerId)
}