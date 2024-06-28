package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.Checklists
import com.webcontrol.android.data.model.Vehiculo
import com.webcontrol.android.data.model.WorkerGMP
import com.webcontrol.android.data.network.ApiResponseAnglo
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface RestInterfaceGmp {
    @GET("api/worker/info/{id}")
    suspend fun getWorker(@Path("id") workerId: String): WorkerGMP

    @POST("v2/checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("v2/checklist")
    fun selectCheckListTest(@Body params: HashMap<String?, String?>?): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("v2/checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>
}