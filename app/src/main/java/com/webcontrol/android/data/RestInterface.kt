package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.Cursos
import com.webcontrol.android.data.db.entity.Preguntas
import com.webcontrol.android.data.model.ResultadosExamen
import com.webcontrol.android.data.network.ApiResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RestInterface {
    @GET("examenes/{rut}")
    fun getListCursos(@Path("rut") rut: String?): Observable<ApiResponse<Cursos>>

    @GET("examenes/preguntas/{idexamen}")
    fun getPreguntas(@Path("idexamen") idexamen: String?): Call<ApiResponse<Preguntas>>

    @POST("examenes/iniciar")
    fun insertResultado(@Body resultado: ResultadosExamen?): Call<ApiResponse<Any>>

    @POST("examenes/terminar")
    fun updateResultado(@Body resultado: ResultadosExamen?): Call<ApiResponse<Any>>

    @POST("examenes/validacion")
    fun validarResultadoByIMEI(@Body resultado: ResultadosExamen?): Call<ApiResponse<List<ResultadosExamen>>>
}