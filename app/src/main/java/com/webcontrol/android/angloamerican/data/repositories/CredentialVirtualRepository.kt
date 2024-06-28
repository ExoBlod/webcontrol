package com.webcontrol.android.angloamerican.data.repositories

import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.dto.CredentialVirtualDTO
import com.webcontrol.android.data.network.CredentialVirtualRequest

class CredentialVirtualRepository(private val api: RestInterfaceAnglo) {
    suspend fun getCredentialVirtual(workerId: String): ApiResponseAnglo<CredentialVirtualDTO> {
        return api.getCredentialVirtual(CredentialVirtualRequest(workerId))
    }
}