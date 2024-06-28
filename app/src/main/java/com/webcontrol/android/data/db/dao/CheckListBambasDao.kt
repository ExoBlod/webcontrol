package com.webcontrol.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webcontrol.android.data.db.entity.*

@Dao
interface CheckListBambasDao {
    @Query("SELECT * FROM INSPECCION_VEHICULAR")
    fun getTnpeccionVehicular():InspeccionVehicular

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInpeccionVehicular(inspeccion: InspeccionVehicular)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(listAnswer: List<NewCheckListQuestions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(listHistory:List<NewCheckListHistorys>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMenuList(listMenu: List<NewCheckListGroups>)

    @Query("SELECT * FROM new_check_list_group")
    fun getGroup(): List<NewCheckListGroups>

    @Query("SELECT * FROM new_check_list_question where id_check_group =:valor")
    fun getAnswer(valor:Int): List<NewCheckListQuestions>

    @Query("SELECT * FROM new_checklist_history where workerId =:workerid")
    fun getHistory(workerid:String): List<NewCheckListHistorys>

    @Query("DELETE FROM new_check_list_question")
    fun clean()

    @Query("SELECT * FROM new_check_list_question WHERE answer = 'NO'")
    fun getAnswerNo():List<NewCheckListQuestions>

    @Query("UPDATE new_check_list_question SET reg_foto = :photo WHERE id_check_group =:grupo AND id_checkDet=:pregunta")
    fun updateQuestion(pregunta:Int,grupo:Int,photo:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSignature(workerSignature:WorkerSignatures)

    @Query("SELECT * FROM worker_signature")
    fun getSignature(): WorkerSignatures
}