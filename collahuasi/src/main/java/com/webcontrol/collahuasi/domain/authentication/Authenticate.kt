package com.webcontrol.collahuasi.domain.authentication

interface IAuthenticate {

    suspend operator fun invoke(request: AuthenticationRequest): AuthenticationResponse
}

class Authenticate(private val repository: IAuthenticationRepository):IAuthenticate{

    override suspend fun invoke(request: AuthenticationRequest): AuthenticationResponse {
        return repository.authenticate(request)
    }
}