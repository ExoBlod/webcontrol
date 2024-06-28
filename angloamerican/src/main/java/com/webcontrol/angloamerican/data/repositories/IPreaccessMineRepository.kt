package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.dto.RegControl
import com.webcontrol.angloamerican.data.model.*
import com.webcontrol.angloamerican.data.network.response.DivisionLB
import com.webcontrol.angloamerican.data.network.response.LocalsAccess
import com.webcontrol.angloamerican.data.network.response.PreaccessMineRequest
import kotlinx.coroutines.flow.Flow

interface IPreaccessMineRepository {
    val lastPreaccess: PreaccesoMina?
    suspend fun getDivionLB(): List<DivisionLB>
    suspend fun getLocalsAccess(): List<LocalsAccess>
    suspend fun postReservationPreaccessMine( list:List<PreaccesoMina>): List<String>
    suspend fun postReservationPreaccessDetailMine( list:List<PreaccesoDetalleMina>): List<String>
    suspend fun getValidationByWorkerId(WorkerId: String, LocalId: String): Boolean
    suspend fun getPreAccessMineAll(): List<PreaccesoMina>
    suspend fun getPreAccessDetailMineAll(): List<PreaccesoDetalleMina>
    suspend fun getDivision(): List<Division>
    suspend fun getLocal(localRequest: LocalRequest): List<Local>
    suspend fun getVehicle(vehicleRequest: VehicleRequest): Vehiculo
    suspend fun postParking(parkingUsage: ParkingUsage): Any
    suspend fun getChecklistTDV(name: String, workerId: String, vehicleId: String, date: String): CheckListTest
    suspend fun getChecklist(name: String, workerId: String, vehicleId: String, date: String): CheckListTest
    suspend fun postChecklist(checkList:CheckListTest): Long
    suspend fun insertPreaccessMine(preaccessMine: PreaccesoMina): Long
    suspend fun insertPreAccessDetailMine(preaccessMine: PreaccesoDetalleMina): Long
    suspend fun getWorkerInfo(request: WorkerRequest): WorkerAnglo
    suspend fun updatePreaccessStatus(preaccessId: Int)
    suspend fun updatePreaccessDetailStatus(preaccessId: Int)
    suspend fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalleMina)
    suspend fun getPreaccessDetailList(preaccessId: Int): List<PreaccesoDetalleMina>
    suspend fun getPreaccessControlList(preaccessId: Int): List<RegControl>
    suspend fun deletePreaccess(preaccessId: Int)
}