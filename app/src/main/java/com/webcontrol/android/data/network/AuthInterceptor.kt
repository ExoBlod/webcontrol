package com.webcontrol.android.data.network

import android.util.Log
import com.webcontrol.android.util.LocalStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val localStorage: LocalStorage,
    private val tokenName: String
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = localStorage[tokenName]
        val request = chain.request().newBuilder()
        Log.d("createInterceptor", "token: Bearer $token")
        request.addHeader("Content-Type", "application/json")
        token?.let {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}