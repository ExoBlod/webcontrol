package com.webcontrol.collahuasi.data.authentication

import com.webcontrol.collahuasi.data.network.Api
import com.webcontrol.collahuasi.domain.authentication.AuthenticationRequest
import com.webcontrol.collahuasi.domain.authentication.AuthenticationResponse
import com.webcontrol.collahuasi.domain.authentication.IAuthenticationRepository

class AuthenticationRepository(private val api: Api) : IAuthenticationRepository {

    override suspend fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        return api.authenticate(request)
    }
}