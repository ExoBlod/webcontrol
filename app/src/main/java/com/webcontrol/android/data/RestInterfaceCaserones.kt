package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.Checklists
import com.webcontrol.android.data.model.WorkerCaserones
import com.webcontrol.android.data.model.WorkerGoldfields
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.LoginRequest
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceCaserones {
    @GET("credential/{rut}")
    fun getWorker(@Path("rut") rut: String): Call<ApiResponseAnglo<WorkerCaserones?>>

    @GET("credentialCertificado/{rut}")
    fun getWorkerCredencial(@Path("rut") rut: String): Call<ApiResponseAnglo<WorkerCaserones>>

    @POST("worker/authenticate")
    fun getToken(@Body params: LoginRequest): Call<ApiResponseAnglo<String>>

    @POST("checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("checklist")
    fun selectCheckListTest(@Body params: HashMap<String, String>): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @GET("checklist/{rut}/{fecha}")
    fun getChecklistByDay(@Path("rut") rut : String, @Path("fecha") fecha : String): Call<ApiResponseAnglo<Int>>

    @GET("telefonocov")
    fun getTelefonoCov() : Call<ApiResponseAnglo<String>>

    @GET("gettime")
    fun getServerTime() : Call<ApiResponseAnglo<String>>
}