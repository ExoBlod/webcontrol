package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceBarrick {

    @GET("worker/divisions")
    suspend fun getDivisions(@Query("workerId") workerId: String): ApiResponseAnglo<ArrayList<Division>>
    @GET("worker/info")
    suspend fun getWorker(@Query("workerId") workerId: String): ApiResponseAnglo<WorkerBarrick>
    @GET("asistencia")
    suspend fun getAttendanceHst(
            @Query("workerId") workerId: String
    ): ApiResponseAnglo<ArrayList<Attendance>>

    @POST("login/authenticate")
    suspend fun getToken(@Body request: LoginRequest): ApiResponseAnglo<String?>

    @POST("asistencia/register")
    suspend fun registerAttendance(@Body request: AttendanceRequest): ApiResponseAnglo<AttendanceResponse>

    @GET("gettime")
    fun getServerTime() : Call<ApiResponseAnglo<String>>

    @GET("checklist/{rut}/{fecha}")
    fun getChecklistByDay(@Path("rut") rut : String, @Path("fecha") fecha : String): Call<ApiResponseAnglo<Int>>

    @POST("checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("checklist")
    fun selectCheckListTest(@Body params: HashMap<String, String>): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @GET("credentialCertificado/{rut}/{country}")
    fun getWorkerCredencial(@Path("rut") rut: String, @Path("country") country: String): Call<ApiResponseBarrick<WorkerCredentialBarrick>>

    @POST("curso/getCursos")
    suspend fun getCourses(@Body courseRequest:CourseRequest):BarrickResponse<List<Capacitaciones>>

    @GET("divisions")
    suspend fun getDivisionList(): BarrickResponse<List<Division>>

}