package com.webcontrol.android.data.clientRepositories

import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.*
import com.webcontrol.android.data.network.dto.*
import com.webcontrol.android.data.network.dto.CredentialVirtualDTO
import com.webcontrol.android.data.network.CredentialVirtualRequest

interface AngloRepository {

    suspend fun getCredentialVirtual(request: CredentialVirtualRequest): ApiResponseAnglo<CredentialVirtualDTO>
    suspend fun getWorkerInfo(request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>

    suspend fun getDivisions(): ApiResponseAnglo<List<Division>>

    suspend fun getLocals(request: LocalRequest): ApiResponseAnglo<List<Local>>

    suspend fun getVehicle(request: VehicleRequest): ApiResponseAnglo<Vehiculo?>

    suspend fun sendPreaccessControlList(list: List<RegControl>): ApiResponseAnglo<Any>

    suspend fun getTime(): ApiResponseAnglo<String>

    suspend fun getLicenseData(rut: String): ApiResponseAnglo<List<DataWorkerDTO>>

    suspend fun getAuthorizationPlaces(rut:String):ApiResponseAnglo<List<AuthorizationPlacesDTO>>

    suspend fun getAuthorizationVehicle(rut:String):ApiResponseAnglo<List<AuthorizedVehicleDTO>>

    suspend fun getDocuments(rut:String):ApiResponseAnglo<List<DocumentValidityDTO>>

    suspend fun getDispatchGuide(activeDispatchGuide:ActiveDispatchGuideRequest):ApiResponseAnglo<List<ActiveDispatchGuideRequestDTO>>
}


class AngloRepositoryImpl(
    val api: RestInterfaceAnglo
): AngloRepository {
     override suspend fun getWorkerInfo(request: WorkerRequest)
            : ApiResponseAnglo<WorkerAnglo?> = api.getWorker(request)

    override suspend fun getDivisions(): ApiResponseAnglo<List<Division>> = api.getDivisions()

    override suspend fun getLocals(request: LocalRequest)
            : ApiResponseAnglo<List<Local>> = api.getLocals(request)

    override suspend fun getVehicle(request: VehicleRequest)
            : ApiResponseAnglo<Vehiculo?> = api.getVehicle(request)

    override suspend fun sendPreaccessControlList(list: List<RegControl>): ApiResponseAnglo<Any> = api.sendControlList(list)

    override suspend fun getTime(): ApiResponseAnglo<String> = api.getTime()

    override suspend fun getLicenseData(rut: String) = api.getLicenseData(rut)

    override suspend fun getCredentialVirtual(request: CredentialVirtualRequest)
            : ApiResponseAnglo<CredentialVirtualDTO> = api.getCredentialVirtual(request)

    override suspend fun getAuthorizationPlaces(rut: String) = api.getAuthorizationPlace(rut)

    override suspend fun getAuthorizationVehicle(rut: String) = api.getAuthorizationWorker(rut)

    override suspend fun getDocuments(rut: String) = api.getDocuments(rut)

    override suspend fun getDispatchGuide(activeDispatchGuide:ActiveDispatchGuideRequest) = api.getDispatchGuide(activeDispatchGuide)
}