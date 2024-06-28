package com.webcontrol.collahuasi.domain.authentication

interface IAuthenticationRepository {

    suspend fun authenticate(request: AuthenticationRequest): AuthenticationResponse
}