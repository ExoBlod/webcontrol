package com.webcontrol.pucobre.data.network

import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.pucobre.data.TOKEN_PUCOBRE
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val localStorage: LocalStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token: String? = localStorage[TOKEN_PUCOBRE]
        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}