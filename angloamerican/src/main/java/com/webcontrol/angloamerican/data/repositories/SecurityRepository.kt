package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import com.webcontrol.angloamerican.data.dto.TokenRequest
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val apiService: AngloamericanApiService
): ISecurityRepository {
    override suspend fun getToken(workerId: String): String {
        val response = apiService.getToken(TokenRequest(workerId))
        return response.data
    }
}