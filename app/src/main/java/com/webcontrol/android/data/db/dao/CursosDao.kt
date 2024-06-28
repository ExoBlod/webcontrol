package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Cursos

@Dao
interface CursosDao {
    @Query("SELECT * FROM cursos " +
            " WHERE idSistema=:idSistema" +
            " AND idCurso=:idCurso" +
            " AND idReserva=:idReserva" +
            " AND rut =:rut ")
    fun getOne(idSistema: String?, idCurso: Int, idReserva: Int, rut: String?): Cursos?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertar(cursos: Cursos?)

    @Query("UPDATE cursos SET  nomCurso =:nomCurso" +
            " , orador=:orador" +
            " , minAprobacion=:minAprobacion" +
            " , fechaHoraCurso=:fechaHoraCurso" +
            " , dia=:dia" +
            " , fechaExamen=:fechaExamen" +
            " WHERE idSistema=:idSistema" +
            " AND idCurso =:idCurso " +
            " AND idReserva =:idReserva " +
            " AND rut =:rut ")
    fun updateCurso(idSistema: String?, idCurso: Int, idReserva: Int, rut: String?, nomCurso: String?, orador: String?, minAprobacion: String?, fechaHoraCurso: String?, dia: Int, fechaExamen: String?)

    @Update
    fun update(cursos: Cursos?)

    @Delete
    fun delete(cursos: Cursos?)

    @Delete
    fun delete(vararg cursos: Cursos?)

    @Query("DELETE FROM cursos")
    fun clean()
}