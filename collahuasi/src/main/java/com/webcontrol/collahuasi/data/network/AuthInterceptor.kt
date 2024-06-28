package com.webcontrol.collahuasi.data.network

import com.webcontrol.collahuasi.domain.common.PREF_API_TOKEN
import com.webcontrol.core.utils.LocalStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val localStorage: LocalStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token: String? = localStorage[PREF_API_TOKEN]
        request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        return chain.proceed(request)
    }
}