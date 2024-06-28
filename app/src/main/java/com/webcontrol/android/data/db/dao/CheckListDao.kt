package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import io.reactivex.Single

@Dao
interface CheckListDao {
    @Query("SELECT * FROM CheckListTest WHERE TipoTest=:tipoTest ORDER BY IdDb DESC")
    fun selectCheckListTestByTipo(tipoTest: String?): Single<List<CheckListTest>>

    @Query("SELECT * FROM CheckListTest WHERE TipoTest=:tipoTest AND CompanyId = :idCompany ORDER BY IdDb DESC")
    fun selectCheckListTestByTipoAndCompany(tipoTest: String?, idCompany: String?=""): Single<List<CheckListTest>>

    @Query("SELECT * FROM CheckListTest WHERE IdDb=:IdDb")
    fun selectCheckListById(IdDb: Int): CheckListTest?

    @Query("SELECT * FROM checklisttest WHERE TipoTest=:tipo and WorkerId=:worker and VehicleId=:vehicle and FechaSubmit=:fecha and EstadoInterno=2 order by IdDb desc limit 1")
    suspend fun selectCheckListTestByTipoAndWorker(tipo: String?, worker: String?, vehicle: String?, fecha: String?): CheckListTest?
    @Query("SELECT * FROM checklisttest WHERE TipoTest=:tipo and WorkerId=:worker and VehicleId=:vehicle and FechaSubmit>=:fecha and EstadoInterno=2 order by IdDb desc limit 1")
    suspend fun selectCheckListTestByTipoTDVAndWorker(tipo: String?, worker: String?, vehicle: String?, fecha: String?): CheckListTest?
    @Query("SELECT * FROM checkListTest_Detalle WHERE IdDB=:idDb and ValorChecked!='NO' order by IdDb desc limit 1")
    suspend fun selectCheckListTestDetalleByIdDb(idDb: Int?): CheckListTest_Detalle?

    @Query("SELECT IdDb FROM CheckListTest WHERE TipoTest=:tipo order by IdDb desc limit 1")
    fun getLastInsertedCheckListTestId(tipo: String?): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCheckListTest(checkListTest: CheckListTest?): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCheckList(checkListTest: CheckListTest)

    @Query("UPDATE CheckListTest SET IdTest=:IdTest,Titulo=:Titulo WHERE IdDb=:IdDb")
    fun updateCheckListTest(IdDb: Int, IdTest: Int, Titulo: String?)

    @Query("UPDATE checklisttest SET EstadoInterno=:estadoInterno , FechaSubmit=:Fecha , HoraSubmit=:Hora WHERE IdDb=:IdDb")
    fun updateCheckListTestSend(IdDb: Int, estadoInterno: Int, Fecha: String?, Hora: String?)

    @Delete
    fun deleteCheckListTest(checkListTest: CheckListTest?)

    @Query("DELETE FROM CheckListTest")
    fun clean()

    @Query("SELECT * FROM CheckListTest_Detalle WHERE IdDb=:IdDb")
    fun selectCheckListTest_DetalleByTest(IdDb: Int): List<CheckListTest_Detalle?>?

    @Query("SELECT * FROM CheckListTest_Detalle WHERE IdDb=:IdDb AND group_id=:groupId")
    fun selectDetalleByTestAndGroupId(IdDb: Int, groupId: Int): List<CheckListTest_Detalle?>?

    @Query("SELECT * FROM CheckListTest_Detalle WHERE IdDb =:IdDB AND case when ValorChecked != 'NO' then 1 else 0 end =:Checked LIMIT 1")
    fun selectDetalleByChecked(IdDB: Int, Checked: Boolean): CheckListTest_Detalle?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCheckListTest_Detalle(checkListTest_detalle: CheckListTest_Detalle?)

    @Query("UPDATE CheckListTest_Detalle SET Checked=:Checked, ValorChecked=:valorChecked WHERE IdDb =:IdDb AND group_id =:IdGroup AND IdTest_Detalle =:IdTest_Detalle AND IdTest=:IdTest")
    fun updateRespuestaCheckListtest_Detalle(IdDb: Int, IdGroup: Int, IdTest: Int, IdTest_Detalle: Int, Checked: Boolean, valorChecked: String = "")

    @Query("UPDATE CheckListTest_Detalle SET ValorSeleccionado=:ValorSeleccionado WHERE IdDb =:IdDb AND group_id =:IdGroup AND IdTest_Detalle =:IdTest_Detalle AND IdTest=:IdTest")
    fun updateValorSeleccionadoCheckListDetalle(IdDb: Int, IdGroup: Int, IdTest: Int, IdTest_Detalle: Int, ValorSeleccionado: String?)

    @Query("DELETE FROM CheckListTest_Detalle WHERE IdDb=:IdDb")
    fun deleteCheckListTestDetalle(IdDb: Int)

    @Query("DELETE FROM CheckListTest_Detalle")
    fun cleanChecklistDetalle()
}