package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.Checklists
import com.webcontrol.android.data.network.ApiResponseAnglo
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RestInterfaceMc {
    @GET("worker/info/{id}")
    suspend fun getWorker(@Path("id") workerId: String): ApiResponseAnglo<Int>

    @POST("worker/authenticate")
    fun getNewToken(@Body params: HashMap<String, String>): Call<ApiResponseAnglo<Any>>

    @POST("checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("checklist")
    fun selectCheckListTest(@Body params: HashMap<String, String>): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @POST("checklist/{rut}/{fecha}")
    fun getChecklistByDay(@Path("rut") rut : String, @Path("fecha") fecha : String): Call<ApiResponseAnglo<Int>>

    @GET("telefonocov")
    fun getTelefonoCov() :Call<ApiResponseAnglo<String>>

    @GET("time")
    fun getServerTime() : Call<ApiResponseAnglo<String>>
}