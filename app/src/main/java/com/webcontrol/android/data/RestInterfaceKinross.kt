package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceKinross {
    @POST("worker")
    fun getWorker(@Body request: WorkerKinross): Call<ApiResponseAnglo<WorkerKinross>>

    @POST("worker/authenticate")
    fun getToken(@Body WorkerKinross: WorkerKinross): Call<ApiResponseAnglo<String>>

    @GET("worker/credencial")
    fun getWorkerCredencial(@Query("workerId") workerId: String): Call<ApiResponseAnglo<WorkerKinrossCredencial>>

    @GET("worker/competencias")
    fun getWorkerCompetencias(@Query("workerId") workerId: String): Call<ApiResponseAnglo<List<Competencia>>>

    @POST("checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("checklist")
    fun selectCheckListTest(@Body params: HashMap<String, String>): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @POST("checklist/savetfs")
    fun sendCheckListTestRespuestasTfs(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @GET("checklist/{rut}/{fecha}")
    fun getChecklistByDay(@Path("rut") rut : String, @Path("fecha") fecha : String): Call<ApiResponseAnglo<Int>>

    @GET("telefonocov")
    fun getTelefonoCov() : Call<ApiResponseAnglo<String>>

    @GET("gettime")
    fun getServerTime() : Call<ApiResponseAnglo<String>>

    @POST("worker")
    fun getWorkerObservable(@Body params: java.util.HashMap<String?, String?>?): Observable<ApiResponseAnglo<WorkerAnglo>>

    @GET("divisions")
    fun selectDivisiones(): Observable<ApiResponseAnglo<List<Division>>>

    @POST("locals")
    fun selectLocales(@Body params: java.util.HashMap<String?, String?>?): Observable<ApiResponseAnglo<List<Local>>>

    @POST("vehicle")
    fun validatePatente(@Body params: java.util.HashMap<String?, String?>?): Observable<ApiResponseAnglo<Vehiculo>>

}