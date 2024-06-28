package com.webcontrol.angloamerican.data.network.service

import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.dto.ReservationRequest
import com.webcontrol.angloamerican.data.dto.TokenRequest
import com.webcontrol.angloamerican.data.model.*
import com.webcontrol.angloamerican.data.network.response.*
import retrofit2.http.*

interface AngloamericanApiService {
    @POST("worker/authenticate")
    suspend fun getToken(@Body tokenRequest: TokenRequest): ApiResponse<String>

    @POST("worker")
    suspend fun getWorker(@Body params: WorkerAngloamerican): ApiResponse<WorkerAngloamerican?>

    @GET("credential/getcredential/{workerId}")
    suspend fun getCredential(@Path("workerId") workerId: String): ApiResponse<WorkerCredential>

    @GET("credential/AuthorizedAreas")
    suspend fun getCredentialAuthorizedAreas(@Query("workerId") workerId: String):ApiResponse<List<AuthorizedAreas>>

    @GET("credential/AuthorizedDivisions")
    suspend fun getCredentialAuthorizedDivisions(@Query("workerId") workerId: String): ApiResponse<List<AuthorizedDivisions>>

    @GET("courses/enabledcourses")
    suspend fun getCourseList(): ApiResponse<List<CourseData>>

    @POST("courses/reservationcourse")
    suspend fun postReservationCourse(@Body params: ReservationRequest): ApiResponse<Boolean>

    @GET("courses/historybyrut")
    suspend fun getHistoryBookCoursesList(@Query("workerId") worker: String): ApiResponse<List<HistoryBookCourseData>>

    @GET("courses/coursesreserves")
    suspend fun getBookedCourses(@Query("workerId") worker: String): ApiResponse<List<BookedCoursesData>>

    @GET("courses/questioncourse")
    suspend fun getQuestionCourse(@Query("IdExam") idExamen: Int): ApiResponse<List<QuestionData>>

    @GET("courses/contentcourse")
    suspend fun getContentCourse(
        @Query("IdCourseProg") idCourseProg: Int,
        @Query("IdExamen") idExam: Int
    ): ApiResponse<List<CourseContentData>>

    @POST("courses/saveanswers")
    suspend fun postAnswers(
        @Query("idCharla") idProgram: Int,
        @Query("IdWorker") idWorker: String,
        @Query("IdEnterprise") enterprise: String,
        @Body listQuestions: List<QuestionData>
    ): ApiResponse<ResultExam>

    @GET("division")
    suspend fun getDivisionLB(): ApiResponse<List<DivisionLB>>

    @GET("localsAcess")
    suspend fun getLocalsAccess(): ApiResponse<List<LocalsAccess>>

    @POST("preaccessmine")
    suspend fun postReservationPreaccessMine(@Body params: List<PreaccesoMina>): ApiResponse<List<String>>

    @POST("preaccessmine")
    suspend fun postReservationPreaccessDetailMine(@Body params: List<PreaccesoDetalleMina>): ApiResponse<List<String>>

    @GET("ValidationByLocalWorkerId")
    suspend fun getValidationByWorkerId(
        @Query("WorkerId") WorkerId: String,
        @Query("Local") Local: String
    ): ApiResponse<List<ResponseValidate>>

    @GET("divisions")
    suspend fun selectDivisiones(): ApiResponse<List<Division>>

    @POST("locals")
    suspend fun selectLocal(@Body localRequest: LocalRequest): ApiResponse<List<Local>>

    @POST("vehicle")
    suspend fun selectVehicle(@Body vehicleRequest: VehicleRequest): ApiResponse<Vehiculo?>

    @POST("parking")
    suspend fun insertParking(@Body parkingUsage: ParkingUsage): ApiResponse<Any>

    @POST("workerWC")
    suspend fun getWorkerInfo(@Body request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>

    @GET("qv/PassesByWorkerId")
    suspend fun getAllMovements(@Query("WorkerId") workerId: String): ApiResponseAnglo<List<Movement>?>

    @GET("qv/InfoPasses")
    suspend fun getMovementDetail(@Query("passes") batchId: String): ApiResponseAnglo<List<MovementDetail>?>

    @POST("qv/AuthorizePasses")
    suspend fun approveMovement(@Body request: ApproveMovementRequest): ApiResponseAnglo<List<ApproveMovementResponse>?>

    @POST("qv/DeclinePasses")
    suspend fun denyMovement(@Body request: DenyMovementRequest): ApiResponseAnglo<List<DenyMovementResponse>?>
}