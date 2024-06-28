package com.webcontrol.angloamerican.data.network

import android.util.Log
import com.webcontrol.angloamerican.data.TOKEN_ANGLO
import com.webcontrol.core.utils.LocalStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor( private val localStorage: LocalStorage): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token: String? = localStorage[TOKEN_ANGLO]
        Log.d("TAG_TOKEN_ANGLO ","$token")
        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}