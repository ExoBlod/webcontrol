package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.CredentialRepository
import javax.inject.Inject

class AuthorizedDivisionsUseCase @Inject constructor(
    private val repository: CredentialRepository
) {
    suspend operator fun invoke(workerId: String) = repository.getAuthDivisions(workerId)
}