package com.webcontrol.pucobre.domain.usecases

import com.webcontrol.pucobre.data.repositories.ICredentialRepository
import javax.inject.Inject

class GetCredentialUseCase @Inject constructor(private val repository: ICredentialRepository) {
    suspend operator fun invoke(workerId: String) = repository.getCredential(workerId)
}