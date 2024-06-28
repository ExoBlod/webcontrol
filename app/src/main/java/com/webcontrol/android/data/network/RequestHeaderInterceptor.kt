package com.webcontrol.android.data.network

import com.webcontrol.android.App
import com.webcontrol.android.util.Constants.TOKEN_BASIC_WEBCONTROLDOCS
import com.webcontrol.android.util.SharedUtils
import okhttp3.Interceptor
import okhttp3.Response

class RequestHeaderInterceptorMc : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenAuthorizationMc(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenAuthorization(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorKS : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenKS(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
class RequestHeaderInterceptorPHC : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${SharedUtils.getTokenAuthorizationPHC(App.getContext())}"
            )
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorYamana : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenYamana(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorWebControlDocs : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", TOKEN_BASIC_WEBCONTROLDOCS)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorBambas : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", "Bearer ${SharedUtils.getTokenBambas(App.getContext())}")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorCaserones : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenCaserones(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorEA : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenEA(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorAnta : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenAnta(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorKinross : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenKinross(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}

class RequestHeaderInterceptorBarrick : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorSgmc : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenSgmc(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorGL : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenGoldfields(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

class RequestHeaderInterceptorPuCobre : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", SharedUtils.getTokenPuCobre(App.getContext()))
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

