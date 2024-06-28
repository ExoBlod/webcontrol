package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.db.dao.CheckListDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoDetalleMinaDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoDetalleMinaDao_Impl
import com.webcontrol.angloamerican.data.db.dao.PreaccesoMinaDao
import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.dto.RegControl
import com.webcontrol.angloamerican.data.model.*
import com.webcontrol.angloamerican.data.network.response.DivisionLB
import com.webcontrol.angloamerican.data.network.response.LocalsAccess
import com.webcontrol.angloamerican.data.network.response.PreaccessMineRequest
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PreaccessMineRepository @Inject constructor(
    private val apiService: AngloamericanApiService,
    private val checklistDao: CheckListDao,
    private val preaccesoMinaDao: PreaccesoMinaDao,
    private val preaccesoDetalleMinaDao: PreaccesoDetalleMinaDao
    ) :
    IPreaccessMineRepository {

    override val lastPreaccess: PreaccesoMina?
        get() = runBlocking {
            preaccesoMinaDao.getLastInserted()
        }

    override suspend fun getDivionLB(): List<DivisionLB> {
        val response = apiService.getDivisionLB()
        return response.data
    }

    override suspend fun getLocalsAccess(): List<LocalsAccess> {
        val response = apiService.getLocalsAccess()
        return response.data
    }

    override suspend fun postReservationPreaccessMine(
        list: List<PreaccesoMina>
    ): List<String> = apiService.postReservationPreaccessMine(list).data

    override suspend fun postReservationPreaccessDetailMine(
        list: List<PreaccesoDetalleMina>
    ): List<String> = apiService.postReservationPreaccessDetailMine(list).data

    override suspend fun getValidationByWorkerId(WorkerId: String, LocalId: String): Boolean {
        val response = apiService.getValidationByWorkerId(WorkerId, LocalId).data
        return response.first().Result
    }

    override suspend fun getPreAccessMineAll() = preaccesoMinaDao.all()

    override suspend fun getPreAccessDetailMineAll() = preaccesoDetalleMinaDao.all()

    override suspend fun getDivision() = apiService.selectDivisiones().data

    override suspend fun getLocal(localRequest: LocalRequest) =
        apiService.selectLocal(localRequest).data

    override suspend fun getVehicle(vehicleRequest: VehicleRequest) =
        apiService.selectVehicle(vehicleRequest).data ?: Vehiculo()

    override suspend fun postParking(parkingUsage: ParkingUsage) =
        apiService.insertParking(parkingUsage)

    override suspend fun insertPreaccessMine(preaccessMine: PreaccesoMina): Long =
        preaccesoMinaDao.insert(preaccessMine)

    override suspend fun insertPreAccessDetailMine(preaccesoDetalleMina: PreaccesoDetalleMina): Long =
        preaccesoDetalleMinaDao.insert(preaccesoDetalleMina)

    override suspend fun getWorkerInfo(request: WorkerRequest): WorkerAnglo =
        apiService.getWorkerInfo(request).data ?: WorkerAnglo()

    override suspend fun getChecklistTDV(
        name: String,
        workerId: String,
        vehicleId: String,
        date: String
    ) = checklistDao.selectCheckListTestByTipoTDVAndWorker(name, workerId, vehicleId, date)
        ?: CheckListTest()

    override suspend fun getChecklist(
        name: String,
        workerId: String,
        vehicleId: String,
        date: String
    ) = checklistDao.selectCheckListTestByTipoTDVAndWorker(name, workerId, vehicleId, date)
        ?: CheckListTest()

    override suspend fun postChecklist(checkList: CheckListTest) =
        checklistDao.insertCheckListTest(checkList)

    override suspend fun updatePreaccessStatus(preaccessId: Int) =
        preaccesoMinaDao.updateStatus(preaccessId)

    override suspend fun updatePreaccessDetailStatus(preaccessId: Int) =
        preaccesoDetalleMinaDao.updateStatus(preaccessId)

    override suspend fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalleMina) =
        preaccesoDetalleMinaDao.insertTemp(preaccessDetail)

    override suspend fun getPreaccessDetailList(preaccessId: Int): List<PreaccesoDetalleMina> =
        preaccesoDetalleMinaDao.getAllByPreaccessId(preaccessId)

    override suspend fun getPreaccessControlList(preaccessId: Int): List<RegControl> =
        preaccesoDetalleMinaDao.getRegControlList(preaccessId)

    override suspend fun deletePreaccess(preaccessId: Int) =
        preaccesoMinaDao.deletePreaccess(preaccessId)
}