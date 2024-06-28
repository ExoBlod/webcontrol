package com.webcontrol.android.data

import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.ui.covid.antapaccay.initialdata.Ciudad
import com.webcontrol.android.ui.covid.antapaccay.initialdata.Comuna
import com.webcontrol.android.ui.covid.antapaccay.initialdata.Pais
import com.webcontrol.android.ui.covid.antapaccay.initialdata.Region
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceAnta {
    @GET("worker")
    suspend fun getWorker(@Query("rut") rut: String): ApiResponseAnglo<WorkerAnta?>

    @POST("auth")
    suspend fun getToken(@Body workerKs: WorkerAnta): ApiResponseAnglo<String>

    @GET("worker/initialdata")
    fun getDatosIniciales(@Query("rut") rut: String): Observable<ApiResponseAnglo<DatosInicialesWorker>>

    @PUT("worker/initialdata")
    fun updateDatosIniciales(@Body data: DatosInicialesWorker?): Observable<ApiResponseAnglo<Any>>

    @GET("covid/controlInicial")
    fun getControlInicial(@Query("rut") rut: String?, @Query("codigo") codigo: String? = null): Call<ApiResponseAnglo<ArrayList<ControlInicial>>>

    @GET("covid/controlcuarentena")
    fun getControlCuarentena(@Query("rut") rut: String, @Query("codControlInicial") id: Int? = null): Call<ApiResponseAnglo<ArrayList<ControlCuarentena>>>

    @PUT("covid/controlcuarentena")
    fun updateControlCuarentena(@Body controlCuarentena: ControlCuarentena): Call<ApiResponseAnglo<String>>

    @GET("covid/cuarentenaDetalle")
    fun getCuarentenaDetalle(@Query("codigo") codigoCuarentena: Int): Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>

    @POST("covid/cuarentenaDetalle")
    fun insertCuarentenaDetalle(@Body cuarentenaDetalle: CuarentenaDetalle): Call<ApiResponseAnglo<String>>

    @GET("covid/djpreguntas")
    fun dJPreguntas(): Call<ApiResponseAnglo<ArrayList<DJPregunta>?>>

    @GET("covid/djconsolidado")
    fun getDJConsolidado(@Query("rut") rut: String, @Query("fecha") fecha: String? = null): Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>

    @PUT("covid/djworker")
    fun sendDJTest(@Body responseList: ArrayList<DJRespuesta>): Call<ApiResponseAnglo<String>>

    @POST("covid/controlInicial")
    fun insertControlInicial(@Body responseList: ArrayList<ControlInicial?>?): Call<ApiResponseAnglo<String>>

    @PUT("covid/controlInicial")
    fun updateControlInicial(@Body responseList: ArrayList<ControlInicial?>?): Call<ApiResponseAnglo<String>>

    @GET("covid/cuestionario")
    fun getCuestionarioByFormat(@Query("formato") codFormato: String): Call<ApiResponseAnglo<ArrayList<Cuestionario>>>

    @POST("covid/cuestionarioWorkers")
    fun sendCuestionario(@Body responseList: ArrayList<CuestionarioResponse>): Call<ApiResponseAnglo<String>>

    @GET("covid/workeraptodj")
    fun getWorkerAptoDJ(@Query("rut") rut: String?, @Query("fecha") fecha: String?): Call<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>

    @GET("app/pais")
    fun getPaises(): Call<ApiResponseAnglo<List<Pais>>>

    @GET("app/region")
    fun getRegiones(@Query("pais") pais: String?): Call<ApiResponseAnglo<List<Region>>>

    @GET("app/ciudad")
    fun getCiudades(@Query("region") region: String?): Call<ApiResponseAnglo<List<Ciudad>>>

    @GET("app/comuna")
    fun getComunas(@Query("ciudad") ciudad: String?): Call<ApiResponseAnglo<List<Comuna>>>
}