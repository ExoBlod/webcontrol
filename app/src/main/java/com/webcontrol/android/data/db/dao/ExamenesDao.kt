package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Examenes
import androidx.room.OnConflictStrategy
@Dao
interface ExamenesDao {
    @Query("SELECT * FROM Examenes " +
            " WHERE idSistema=:idSistema" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idExamen=:idExamen" +
            " AND idReserva=:idReserva" +
            " AND rut =:rut ")
    fun getOne(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): Examenes?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertar(examenes: Examenes?)

    @Query("UPDATE Examenes SET iniciado = 1 " +
            " WHERE idSistema=:idSistema" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idExamen =:idExamen" +
            " AND idReserva=:idReserva" +
            " AND rut=:rut")
    fun setExamenIniciado(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): Int

    @Query("UPDATE Examenes SET nomExamen=:nomExamen " +
            " , nomEncuesta =:nomEncuesta" +
            " , idEncuesta =:idEncuesta" +
            " , descExamen=:descExamen" +
            " , fecha_programada=:fecha_programada " +
            " , hora_programada =:hora_programada" +
            " , tiempoTotal=:tiempoTotal " +
            " , estado =:estado " +
            " , aprobo=:aprobo" +
            " , tipo=:tipo" +
            " , orden=:orden" +
            " , examenFinal=:examenFinal" +
            " , dia=:dia" +
            " , fechaExamen=:fechaExamen" +
            " WHERE idSistema=:idSistema" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idExamen =:idExamen " +
            " AND idReserva =:idReserva " +
            " AND rut =:rut ")
    fun updateExamenes(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?, nomExamen: String?, idEncuesta: Int, nomEncuesta: String?, descExamen: String?, fecha_programada: String?, hora_programada: String?
                       , tiempoTotal: String?, aprobo: String?, estado: Int, tipo: String?, orden: Int, examenFinal: Int, dia: Int, fechaExamen: String?)

    @Update
    fun update(examenes: Examenes?)

    @Delete
    fun delete(examenes: Examenes?)

    @Delete
    fun delete(vararg examenes: Examenes?)

    @Query("DELETE FROM Examenes")
    fun clean()
}