package com.webcontrol.android.ui.preacceso

import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.db.dao.CheckListDao
import com.webcontrol.android.data.db.dao.PreaccesoDao
import com.webcontrol.android.data.db.dao.PreaccesoDetalleDao
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.RegControl
import com.webcontrol.angloamerican.data.dto.Checklist
import kotlinx.coroutines.flow.Flow

class PreaccesoRepositoryImpl(
    private val api: RestInterfaceAnglo,
    private val preaccesoDao: PreaccesoDao,
    private val preaccesoDetalleDao: PreaccesoDetalleDao,
    private val checklistDao: CheckListDao
) : PreaccesoRepository {

    override val lastPreaccess: Flow<Preacceso?> = preaccesoDao.getLastInserted()

    override suspend fun getChecklist(
        type: String?,
        workerId: String?,
        vehicleId: String?,
        date: String?
    ): CheckListTest? =
        checklistDao.selectCheckListTestByTipoAndWorker(type, workerId, vehicleId, date)

    override suspend fun getChecklistTDV(
        type: String?,
        workerId: String?,
        vehicleId: String?,
        date: String?
    ): CheckListTest? =
        checklistDao.selectCheckListTestByTipoTDVAndWorker(type, workerId, vehicleId,date)
    override suspend fun getChecklistDetail(
        idDb:Int?
    ): CheckListTest_Detalle? =
        checklistDao.selectCheckListTestDetalleByIdDb(idDb)

    override fun getPreaccessDetailList(preaccessId: Int)
            : Flow<List<PreaccesoDetalle>> = preaccesoDetalleDao.getAllByPreaccessId(preaccessId)

    override suspend fun getPreaccessControlList(preaccessId: Int)
            : List<RegControl> = preaccesoDetalleDao.getRegControlList(preaccessId)

    override suspend fun insertPreaccess(preaccess: Preacceso): Long =
        preaccesoDao.insert(preaccess)

    override suspend fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalle) =
        preaccesoDetalleDao.insertTemp(preaccessDetail)

    override suspend fun insertChecklistTest(checkListTest: CheckListTest) =
        checklistDao.insertCheckList(checkListTest)

    override suspend fun updatePreaccessStatus(preaccessId: Int) =
        preaccesoDao.updateStatus(preaccessId)

    override suspend fun deletePreaccess(preaccess: Preacceso) = preaccesoDao.delete(preaccess)

    override suspend fun getRemoteChecklist(
        type: String,
        workerId: String,
        vehicleId: String,
        date: String
    ): Checklist? {
        val response = api.getChecklist(type, date, workerId)
        for (item in response.data) {
            if (item.vehicleId.uppercase() == vehicleId.uppercase()) {
                return item
            }
        }
        return null
    }
}