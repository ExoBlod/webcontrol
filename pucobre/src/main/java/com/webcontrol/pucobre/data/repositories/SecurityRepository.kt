package com.webcontrol.pucobre.data.repositories

import android.util.Log
import com.webcontrol.pucobre.data.PucobreApiService
import com.webcontrol.pucobre.data.dto.TokenRequest
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val apiService: PucobreApiService
) : ISecurityRepository {
    override suspend fun getToken(workerId: String): String {
        val response = apiService.getToken(TokenRequest(workerId))
        Log.i("getToken",response.toString())
        return response.data
    }
}