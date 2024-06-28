package com.webcontrol.android.ui.preacceso

import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.RegControl
import com.webcontrol.angloamerican.data.dto.Checklist
import kotlinx.coroutines.flow.Flow

interface PreaccesoRepository {
    val lastPreaccess: Flow<Preacceso?>

    suspend fun getChecklist(
        type: String?,
        workerId: String?,
        vehicleId: String?,
        date: String?
    ): CheckListTest?

    suspend fun getChecklistTDV(
        type: String?,
        workerId: String?,
        vehicleId: String?,
        date: String?
    ): CheckListTest?

    suspend fun getChecklistDetail(
        idDb: Int?
    ): CheckListTest_Detalle?

    fun getPreaccessDetailList(preaccessId: Int): Flow<List<PreaccesoDetalle>>

    suspend fun getPreaccessControlList(preaccessId: Int): List<RegControl>

    suspend fun insertPreaccess(preaccess: Preacceso): Long

    suspend fun insertPreaccessDetail(preaccessDetail: PreaccesoDetalle)

    suspend fun insertChecklistTest(checkListTest: CheckListTest)

    suspend fun updatePreaccessStatus(preaccessId: Int)

    suspend fun deletePreaccess(preaccess: Preacceso)

    suspend fun getRemoteChecklist(
        type: String,
        workerId: String,
        vehicleId: String,
        date: String
    ): Checklist?
}