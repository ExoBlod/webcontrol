package com.webcontrol.angloamerican.ui.checklistpreuso.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.*
import com.webcontrol.angloamerican.ui.checklistpreuso.data.model.InspectionHistory
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse


@Dao
interface CheckListPreUsoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(listhistory:List<InspectionHistory>)

    @Query("SELECT * FROM inspection_history where worker_id =:workerid")
    fun getHistory(workerid:String): List<InspectionHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSignature(workerSignature: WorkerSignature)

    @Query("SELECT * FROM signature")
    fun getSignature(): List<WorkerSignature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(listAnswer: List<NewCheckListQuestions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMenuList(listMenu: List<NewCheckListGroups>)

    @Query("DELETE FROM new_check_list_question")
    fun clean()

    @Query("SELECT * FROM new_check_list_group")
    fun getGroup(): List<NewCheckListGroups>

    @Query("SELECT * FROM new_check_list_question where id_check_group =:valor")
    fun getAnswer(valor:Int): List<NewCheckListQuestions>

    @Query("SELECT * FROM inspeccion_vehicular")
    fun getTnpeccionVehicular(): InspeccionVehicular

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInpeccionVehicular(inspeccion: InspeccionVehicular)

   /*
    @Query("SELECT * FROM new_check_list_question WHERE answer = 'NO'")
    fun getAnswerNo():List<NewCheckListQuestions>

    @Query("UPDATE new_check_list_question SET reg_foto = :photo WHERE id_check_group =:grupo AND id_checkDet=:pregunta")
    fun updateQuestion(pregunta:Int,grupo:Int,photo:String)*/
}