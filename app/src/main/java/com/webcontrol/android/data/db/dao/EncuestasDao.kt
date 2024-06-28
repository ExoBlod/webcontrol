package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Encuestas
import io.reactivex.Single
import androidx.room.FtsOptions.Order

import androidx.room.OnConflictStrategy

@Dao
interface EncuestasDao {
    @Query("SELECT * FROM Encuestas WHERE examType=:examType GROUP BY PadreId ORDER BY IdDb DESC")
    fun selectEncuestasByTipo(examType: String?): Single<List<Encuestas>>

    @Query("SELECT * FROM Encuestas WHERE PadreId=:padreId  ORDER BY IdDb ASC")
    fun selectEncuestasByPadreId(padreId: Int?): List<Encuestas>

    @Query("SELECT * FROM Encuestas WHERE IdDb=:IdDb")
    fun selectCheckListById(IdDb: Int): Encuestas?

    @Query("SELECT IdDb FROM Encuestas WHERE examType=:tipo order by IdDb desc limit 1")
    fun getLastInsertedEncuestasId(tipo: String?): Int?

    @Query("SELECT PadreId FROM Encuestas WHERE ExamId=:examId order by IdDb desc limit 1")
    fun getLastInsertedEncuestasIdPadre(examId: Int): Int?

    @Query("UPDATE Encuestas SET Estado = 2 WHERE PadreId =:idPadre")
    fun updatedEncuestasId(idPadre: Int)

    @Query("UPDATE Encuestas SET WorkerId =:answerId  WHERE PadreId =:idPadre and QuestionId=:questionId")
    fun updatedEncuestasIdandAnswer(idPadre: Int,questionId: Int,answerId:String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEncuestas(Encuestas: Encuestas?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllEncuestas(order: List<Encuestas?>?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCheckList(Encuestas: Encuestas)

    @Delete
    fun deleteEncuestas(Encuestas: Encuestas?)

    @Query("DELETE FROM Encuestas")
    fun clean()
}