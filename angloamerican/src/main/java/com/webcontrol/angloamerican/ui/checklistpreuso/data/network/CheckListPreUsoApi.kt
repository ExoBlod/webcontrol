package com.webcontrol.angloamerican.ui.checklistpreuso.data.network

import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListGroup
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.ApiResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListByIdRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListByIdResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.UploadEvidenceRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.UploadEvidenceResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleInformationResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CheckListPreUsoApi {

    @POST("checklistPreUso/getcredentialsearchuser")
    suspend fun getCredentialSearchUser(@Body request: WorkerRequest): ApiResponse<List<WorkerResponse>>

    @GET("checklistPreUso/history")
    suspend fun getHistory(@Query("WorkerId") request: String): ApiResponse<List<HistoryResponse>>

    @GET("checklistPreUso/vehicleinformation")
    suspend fun getVehicleInformation(@Query("PlateCar") request: String): ApiResponse<List<VehicleInformationResponse>>

    @POST("checklistPreUso/insertinspection")
    suspend fun insertInspection(@Body request: InsertInspectionRequest): ApiResponse<List<InsertInspectionResponse>>

    @POST("checklistPreUso/setsignature")
    suspend fun setSignature(@Body request: SignatureRequest): ApiResponse<List<SignatureResponse>>

    @GET("checklistPreUso/listquestions")
    suspend fun getQuestions(@Query("TypeVehicle") request: String): ApiResponse<List<QuestionListResponse>>

    @POST("checklistPreUso/uploadevidence")
    suspend fun uploadEvidence(@Body request: UploadEvidenceRequest): ApiResponse<List<UploadEvidenceResponse>>

    @POST("checklistPreUso/saveanswers")
    suspend fun saveAnswers(@Body request: List<QuestionListResponse>): ApiResponse<List<SaveAnswersResponse>>

    @GET("checklistPreUso/listquestionsbyidcheckhead")
    suspend fun getQuestionsById(@Body request: QuestionListByIdRequest): ApiResponse<List<QuestionListByIdResponse>>

    @GET("checklistPreUso/listquestionsbyidcheckhead")
    suspend fun getQuestionsByIdHead(@Query ("IdCheckInHead") idHead: Int): ApiResponse<List<QuestionListResponse>>

    @GET("checklistPreUso/historybyworkerid")
    suspend fun getHistoryByWorkerId(@Query ("WorkerId") workerId : String , @Query ("DateQuery") dateQuery : String): ApiResponse<List<HistoryResponse>>

    @GET("checklistPreUso/historybyplate")
    suspend fun getHistoryByPlate(@Query ("Plate") plate : String , @Query ("DateQuery") dateQuery : String): ApiResponse<List<HistoryResponse>>
}
