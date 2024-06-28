package com.webcontrol.android.data.clientRepositories

import com.webcontrol.android.data.RestInterfaceCollahuasi
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.LocalRequest
import com.webcontrol.android.data.network.VehicleRequest
import com.webcontrol.android.data.network.WorkerRequest

interface CollahuasiRepository {
    suspend fun getWorkerInfo(request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>

    suspend fun login(request: WorkerRequest): ApiResponseAnglo<String?>

    suspend fun getDivisions(): ApiResponseAnglo<List<Division>>

    suspend fun getLocals(request: LocalRequest): ApiResponseAnglo<List<Local>>

    suspend fun getVehicle(request: VehicleRequest): ApiResponseAnglo<Vehiculo?>

    suspend fun sendPreaccessControlList(list: List<RegControl>): ApiResponseAnglo<Any>
}

class CollahuasiRepositoryImpl(
    val api: RestInterfaceCollahuasi
): CollahuasiRepository {
    override suspend fun getWorkerInfo(request: WorkerRequest)
        : ApiResponseAnglo<WorkerAnglo?> = api.getWorker(request)

    override suspend fun login(request: WorkerRequest): ApiResponseAnglo<String?> = api.login(request)

    override suspend fun getDivisions(): ApiResponseAnglo<List<Division>> = api.getDivisions()

    override suspend fun getLocals(request: LocalRequest)
            : ApiResponseAnglo<List<Local>> = api.getLocals(request)

    override suspend fun getVehicle(request: VehicleRequest)
            : ApiResponseAnglo<Vehiculo?> = api.getVehicle(request)

    override suspend fun sendPreaccessControlList(list: List<RegControl>): ApiResponseAnglo<Any> = api.sendControlList(list)
}