package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.*
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.*
import com.webcontrol.android.data.network.dto.*
import com.webcontrol.angloamerican.data.*
import com.webcontrol.angloamerican.data.dto.Checklist
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceAnglo {
    @POST("checklist/list")
    fun getChecklists(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Checklists>>>

    @POST("checklist")
    fun selectCheckListTest(@Body params: HashMap<String?, String?>?): Observable<ApiResponseAnglo<CheckListTest>>

    @POST("vehicle")
    fun validatePatente(@Body params: HashMap<String?, String?>?): Observable<ApiResponseAnglo<Vehiculo>>

    @POST("checklist/save")
    fun sendCheckListTestRespuestas(@Body checkListTests: CheckListTest?): Observable<ApiResponseAnglo<Any>>

    @GET("divisions")
    fun selectDivisiones(): Observable<ApiResponseAnglo<List<Division>>>

    @GET("companies")
    fun selectCompanies(): Observable<ApiResponseAnglo<List<Company>>>

    @POST("locals")
    fun selectLocales(@Body params: HashMap<String?, String?>?): Observable<ApiResponseAnglo<List<Local>>>

    @POST("worker")
    fun getWorkerObservable(@Body params: HashMap<String?, String?>?): Observable<ApiResponseAnglo<WorkerAnglo>>

    @POST("worker/pase")
    fun blockWorkerPass(@Body workerPase: WorkerPase?): Observable<ApiResponseAnglo<Any>>

    @GET("initialData/{rut}")
    fun getDatosIniciales(@Path("rut") rut: String?): Observable<ApiResponseAnglo<DatosInicialesWorker>>

    @POST("updateInitialData")
    fun updateDatosIniciales(@Body data: DatosInicialesWorker?): Observable<ApiResponseAnglo<Any>>

    @GET("divisions")
    suspend fun getDivisions(): ApiResponseAnglo<List<Division>>

    @POST("localsPreAcceso")
    suspend fun getLocals(@Body request: LocalRequest): ApiResponseAnglo<List<Local>>

    @GET("companies")
    fun empresas(): Call<ApiResponseAnglo<List<Company>>>

    @POST("vehicle")
    suspend fun getVehicle(@Body request: VehicleRequest): ApiResponseAnglo<Vehiculo?>

    @POST("worker")
    fun getWorker(@Body params: HashMap<String, String>): Call<ApiResponseAnglo<WorkerAnglo>>

    @POST("workerWC")
    suspend fun getWorker(@Body request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>

    @POST("preaccess")
    suspend fun sendControlList(@Body list: List<RegControl>): ApiResponseAnglo<Any>

    @POST("worker/authenticate")
    fun getNewToken(@Body params: HashMap<String, String>): Call<ApiResponseAnglo<Any>>

    @GET("twlat/{rut}/{room}")
    fun getTwilioToken(
        @Path("rut") rut: String?,
        @Path("room") room: String?
    ): Call<ApiResponseAnglo<Any>>

    @POST("geofences")
    fun geofences(): Call<ApiResponseAnglo<List<PuntoMarcacion>>>

    @POST("worker/pase")
    fun sendBlockPass(@Body workerPase: WorkerPase?): Call<ApiResponseAnglo<String>>

    // COVID
    @GET("cuestionario/{testFormat}")
    fun getCuestionarioByFormat(@Path("testFormat") codFormato: String?): Call<ApiResponseAnglo<ArrayList<Cuestionario>>>

    @GET("djWorkerCD/{rut}/{fecha}")
    fun getDJConsolidado(
        @Path("rut") rut: String?,
        @Path("fecha") fecha: String?
    ): Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>

    @GET("djPreguntas")
    fun dJPreguntas(): Call<ApiResponseAnglo<ArrayList<DJPregunta>?>>

    @POST("updateDJWorker")
    fun sendDJTest(@Body responseList: ArrayList<DJRespuesta>): Call<ApiResponseAnglo<String>>

    @POST("insertCuestionario")
    fun sendCuestionario(@Body responseList: ArrayList<CuestionarioResponse>): Call<ApiResponseAnglo<String>>

    @GET("controlInicial/{rut}/{codigo}")
    fun getControlInicial(
        @Path("rut") rut: String?,
        @Path("codigo") codigo: String?
    ): Call<ApiResponseAnglo<ArrayList<ControlInicial>>>

    @POST("insertControlInicial")
    fun insertControlInicial(@Body responseList: ArrayList<ControlInicial?>?): Call<ApiResponseAnglo<String>>

    @POST("updateControlInicial")
    fun updateControlInicial(@Body responseList: ArrayList<ControlInicial?>?): Call<ApiResponseAnglo<String>>

    @GET("ctrlcuarentena/{rut}/{cdctrlin}")
    fun getControlCuarentena(
        @Path("rut") rut: String,
        @Path("cdctrlin") id: Int? = null
    ): Call<ApiResponseAnglo<ArrayList<ControlCuarentena>>>

    @POST("updateCtrlCuarentena")
    fun updateControlCuarentena(@Body controlCuarentena: ControlCuarentena): Call<ApiResponseAnglo<String>>

    @GET("cuarentenaDet/{cod}")
    fun getCuarentenaDetalle(@Path("cod") codigoCuarentena: Int): Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>

    @POST("insertCuarentenaDtll")
    fun insertCuarentenaDetalle(@Body cuarentenaDetalle: CuarentenaDetalle): Call<ApiResponseAnglo<String>>

    @POST("rutas/rutadia")
    fun getRutaDia(@Body params: HashMap<String?, String?>?): Call<ApiResponseAnglo<List<Ruta>>>

    @GET("listPais")
    fun paises(): Call<ApiResponseAnglo<List<Pais>>>

    @GET("regionPais/{pais}")
    fun getRegiones(@Path("pais") pais: String?): Call<ApiResponseAnglo<List<RegionPais>>>

    @GET("ciudadRegion/{region}")
    fun getCiudades(@Path("region") region: String?): Call<ApiResponseAnglo<List<CiudadRegion>>>

    @GET("comunaCiudad/{ciudad}")
    fun getComunas(@Path("ciudad") ciudad: String?): Call<ApiResponseAnglo<List<ComunaCiudad>>>

    @GET("workerAptoDJ")
    fun getWorkerAptoDJ(
        @Query("rut") rut: String?,
        @Query("fecha") fecha: String?
    ): Call<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>

    @GET("licencias/conducir/datos")
    suspend fun getLicenseData(
        @Query("rut") rut:String
    ): ApiResponseAnglo<List<DataWorkerDTO>>

    @GET("licencias/conducir/autorizacion/lugar")
    suspend fun getAuthorizationPlace(
        @Query("rut") rut:String
    ): ApiResponseAnglo<List<AuthorizationPlacesDTO>>

    @GET("licencias/conducir/vehiculos/autorizados")
    suspend fun getAuthorizationWorker(
        @Query("rut") rut:String
    ): ApiResponseAnglo<List<AuthorizedVehicleDTO>>

    @GET("licencias/conducir/vigencia/documentos")
    suspend fun getDocuments(
        @Query("rut") rut:String
    ): ApiResponseAnglo<List<DocumentValidityDTO>>



    @GET("checklist/{rut}/{fecha}")
    fun getChecklistByDay(
        @Path("rut") rut: String,
        @Path("fecha") fecha: String
    ): Call<ApiResponseAnglo<Int>>

    @GET("parameter/{id}")
    suspend fun getParameter(@Path("id") id: String): ApiResponseAnglo<List<Parameter>>

    @GET("parking/{date}")
    suspend fun getAvailableParkingSpaces(@Path("date") date: String): ApiResponseAnglo<List<ParkingResponse>>

    @POST("guiadespacho/validation")
    suspend fun getDispatchGuide(@Body getDispatchGuide: ActiveDispatchGuideRequest): ApiResponseAnglo<List<ActiveDispatchGuideRequestDTO>>

    @POST("guiadespacho/save")
    @Multipart
    fun insertFileOwnDocs(
        @Part("ID") id: String,
        @Part("CARA") cara: Int,
        @Part ARCHIVO: MultipartBody.Part
    ): Call<ApiResponseAnglo<String>>

    @POST("parking")
    suspend fun insertParking(@Body parkingUsage: ParkingUsage): ApiResponseAnglo<Any>

    @POST("examen/list")
    fun getExamList(@Body ExamType: RequestExam): Call<ApiResponseAnglo<List<EncuestasList>>>

    @POST("examen")
    fun getExamByExamId(@Body params: RequestExam): Observable<ApiResponseAnglo<ResponseExam>>

    @POST("examen/save")
    fun saveExam(@Body exam: ResponseExam): Observable<ApiResponseAnglo<Any>>

    @POST("gettime")
    fun getTime(): ApiResponseAnglo<String>

    @POST("reserva-bus/getlistbyrut")
    fun getReservasBusByRut(@Body request: RequestReservaBus): Observable<ApiResponseAnglo<List<ReservaBus>>>

    @POST("qv/course/getHistoryReserves")
    fun getHistoryReserves(@Body request: RequestReservaCurso): Observable<ApiResponseAnglo<List<ReservaCurso>>>

    @GET("qv/course/list")
    fun getCourseList(): Call<ApiResponseAnglo<List<ReservaCurso>>>


    @POST("qv/course/insertReserve")
    fun insertReserve(@Body request: ReserveCourseRequest): Call<ApiResponseAnglo<String>>

    @POST("qv/course/deleteReserve")
    fun deleteReserve(@Body request: ReserveCourseRequest): Call<ApiResponseAnglo<String>>

    @POST("qv/course/getCredentialCourses")
    suspend fun getCoursesCredential(@Body params: CredentialCourseRequest): ApiResponseAnglo<List<CredentialCourse>>

    @POST("qv/credential/getcredentialvirtual")
    suspend fun getCredentialVirtual(@Body request: CredentialVirtualRequest): ApiResponseAnglo<CredentialVirtualDTO>

    @POST("reserva-bus/getSourcesByDivition")
    suspend fun getSources(@Body request: RequestReservaBus): ApiResponseAnglo<List<SourceReservaBus>>

    @POST("reserva-bus/getDestinyByDivition")
    suspend fun getDestinies(@Body request: RequestReservaBus): ApiResponseAnglo<List<DestinyReservaBus>>

    @POST("reserva-bus/getScheduleReservaBus")
    suspend fun getBusesAvailable(@Body request: RequestReservaBus): ApiResponseAnglo<List<ResponseReservaBus>>

    @POST("reserva-bus/getHistoryByRutReservaBus")
    fun getHistoryReservesBus(@Body request: RequestReservaBus): Observable<ApiResponseAnglo<List<ReservaBus2>>>

    @POST("reserva-bus/getSeatsOfBus")
    suspend fun getSeatsBus(@Body request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseSeatBus>>

    @POST("reserva-bus/insertReservaBus")
    suspend fun insertReserveBus(@Body request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseReservaBus>>

    @POST("reserva-bus/cancelReservaBus")
    suspend fun cancelReserveBus(@Body request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseReservaBus>>

    @GET("checklist/{tipo}/{fecha}/{rut}")
    suspend fun getChecklist(
        @Path("tipo") tipo: String,
        @Path("fecha") fecha: String,
        @Path("rut") rut: String
    ): ApiResponseAnglo<List<Checklist>>

}