package com.webcontrol.android.util

import com.webcontrol.android.App.Companion.getContext
import com.webcontrol.android.FirebaseMessageInterface
import com.webcontrol.android.R
import com.webcontrol.android.data.*
import com.webcontrol.android.data.network.*
import com.webcontrol.pucobre.data.PucobreApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestClient {
    @JvmStatic
    fun buildL(): RestInterfaceWC {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_mensajeria))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceWC::class.java)
    }

    @JvmStatic
    fun buildM(): FirebaseMessageInterface {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_mensajeria))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(FirebaseMessageInterface::class.java)
    }

    @JvmStatic
    fun build(sistema: String): RestInterface {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(
                if (sistema === Constants.QBLANCA) getContext()!!.getString(R.string.ws_url) else getContext()!!.getString(
                    R.string.ws_url_yamana
                )
            )
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterface::class.java)
    }

    @JvmStatic
    fun buildRx(sistema: String): RestInterface {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(
                if (sistema === Constants.QBLANCA) getContext()!!.getString(R.string.ws_url) else getContext()!!.getString(
                    R.string.ws_url_yamana
                )
            )
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterface::class.java)
    }

    fun buildAngloRx(): RestInterfaceAnglo {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_anglo))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceAnglo::class.java)
    }

    @JvmStatic
    fun buildAnglo(): RestInterfaceAnglo {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_anglo))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceAnglo::class.java)
    }

    fun buildGmpRx(): RestInterfaceGmp {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_gmp))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceGmp::class.java)
    }

    @JvmStatic
    fun buildGmp(): RestInterfaceGmp {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_gmp))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceGmp::class.java)
    }

    @JvmStatic
    fun buildMc(): RestInterfaceMc {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorMc())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_mc))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceMc::class.java)
    }

    @JvmStatic
    fun buildCdl(): RestInterfaceCandelaria {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_cdl))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceCandelaria::class.java)
    }


    fun buildKs(): RestInterfaceKs {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorKS())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_ks))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceKs::class.java)
    }

    fun buildYamana(): RestInterfaceYamana {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorYamana())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_yamanacre))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceYamana::class.java)
    }

    fun buildBambas(): RestInterfaceBambas {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorBambas())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_bambas))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceBambas::class.java)
    }

    fun buildCaserones(): RestInterfaceCaserones {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorCaserones())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_caseronescre))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(RestInterfaceCaserones::class.java)
    }

    fun buildEA(): RestInterfaceEA {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorEA())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_ea))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceEA::class.java)
    }

    fun buildAnta(): RestInterfaceAnta {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorAnta())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_anta))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceAnta::class.java)
    }

    fun buildAntaRx(): RestInterfaceAnta {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorAnta())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_anta))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceAnta::class.java)
    }

    fun buildKinross(): RestInterfaceKinross {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorKinross())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_kinross))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(RestInterfaceKinross::class.java)
    }


    fun buildGf(): RestInterfaceGoldfields {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorGL())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_goldfields))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceGoldfields::class.java)
    }

    fun buildBarrick(): RestInterfaceBarrick {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorBarrick())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_barrick))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfaceBarrick::class.java)
    }

    fun buildSgscm(): RestInterfaceSgscm {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorSgmc())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_sgmc))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceSgscm::class.java)
    }

    fun buildWebControlDocs(): RestInterfaceWCDocs {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorWebControlDocs())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_webcondocs))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestInterfaceWCDocs::class.java)
    }

    fun buildPucobre(): PucobreApiService {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorPuCobre())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_pucobre))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(PucobreApiService::class.java)
    }

    @JvmStatic
    fun buildPHC(): RestInterfacePHC {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(RequestHeaderInterceptorPHC())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getContext()!!.getString(R.string.ws_url_phc))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()
        return retrofit.create(RestInterfacePHC::class.java)
    }
}