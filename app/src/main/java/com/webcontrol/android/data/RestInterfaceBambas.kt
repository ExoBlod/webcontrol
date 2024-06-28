package com.webcontrol.android.data

import com.webcontrol.android.data.model.DocumentsBambas
import com.webcontrol.android.data.model.WorkerBambas
import com.webcontrol.android.data.network.ApiResponseBambas
import com.webcontrol.android.data.network.ApiResponseBambasCredential
import com.webcontrol.android.data.network.ApiResponseSearchBambas
import com.webcontrol.android.data.network.TokenBambasRequest
import com.webcontrol.android.ui.newchecklist.data.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestInterfaceBambas {
    @POST("auth")
    suspend fun getToken(@Body request: TokenBambasRequest): ApiResponseBambas

    @GET("worker/credentialvirtual")
    suspend fun getWorkerCredencial(@Query ("workerId") worker: String): ApiResponseBambasCredential<WorkerBambas>

    @GET("worker/Documentos")
    suspend fun getDocumentos(@Query("workerId")worker: String,@Query("paseId")paseId: Int):ApiResponseBambasCredential<List<DocumentsBambas>>

    @GET("worker/credentialsearchuser")
    suspend fun getWorkerSearchCredencial(@Query ("workerId") worker: String): List<ApiResponseSearchBambas>

    @GET("worker/checklistrecord")
    suspend fun getWorkerHistory(@Query ("workerId") worker: String): List<NewCheckListHistory>

    @POST("worker/SetSignature")
    suspend fun setSignatureWorker(@Body workerSignature: WorkerSignature): Boolean

    @GET("worker/vehicleinformation")
    suspend fun getModelbyPlate(@Query ("PlateCar") plateCar: String): VehicleInformation

    @POST("worker/insertvehicleinformation")
    suspend fun insertWorkerVehicleInformation(@Body workerVehicleInformation : WorkerVehicleInformation): AnswerCheckingHead

    @POST("worker/InsertResponseQuestion")
    suspend fun insertEvidenceInCheckDetail(@Body requestEvidenceInsert: RequestEvidenceInsert): Boolean

    @GET("worker/getlistevidence")
    suspend fun getListEvidence(@Query ("workerID") worker: String, @Query ("valor") valor: String): List<EvidenceList>

    @POST("worker/uploadevidence")
    suspend fun postPhotoEvidence(@Body newCheckListGroup: NewCheckListGroup): Boolean

    @GET("worker/questionbyanswerno")
    suspend fun QuestionByNo(@Query ("workerId") worker: String, @Query ("IdCheck") IdCheck: Int): List<AnswersQuestion>

    @POST("worker/insertevidence")
    suspend fun insertEvidence(@Body requestEvidenceInformation: RequestEvidenceInformation): Boolean

    @GET("worker/checklistrecordbyworkerid")
    suspend fun getWorkerHistoryById(@Query ("workerId") worker: String, @Query ("WorkerSearchId") workerSearchId: String, @Query ("Fecha") date: String, @Query ("Filter") filter: Boolean): List<NewCheckListHistory>

    @GET("worker/checklistrecordbyplate")
    suspend fun getWorkerHistoryByPlate(@Query ("Plate") plate: String,  @Query ("WorkerSearchId") workerSearchId: String, @Query ("Fecha") date: String, @Query ("Filter") filter: Boolean): List<NewCheckListHistory>

    @GET("worker/listquestions")
    suspend fun questionLists(): List<NewCheckListGroup>

    @POST("worker/saveanswers")
    suspend fun savingAnswer(@Body listAnswer:List<NewCheckListGroup>): List<NewCheckListQuestion>

   @GET("worker/listquestionsbycheckingheadid")
    suspend fun getListQuestionByCheckingHead(@Query ("IdHead") idHead: Int): List<NewCheckListGroup>
}