package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.ISecurityRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val repository: ISecurityRepository)  {
    suspend operator fun invoke(workerId: String) = repository.getToken(workerId)
}