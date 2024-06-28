package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Preguntas

@Dao
interface PreguntasDao {
    @Query("SELECT * FROM preguntas " +
            "WHERE idPregunta=:idPregunta" +
            " AND idExamen =:idExamen " +
            " AND idReserva=:idReserva " +
            " AND rut=:rut " +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idSistema=:idSistema")
    fun getOne(idSistema: String?, idExamenReserva: Int, idPregunta: Int, idExamen: Int, idReserva: Int, rut: String?): Preguntas?

    @Query("SELECT * FROM preguntas " +
            " WHERE idExamen =:idExamen " +
            " AND idReserva=:idReserva " +
            " AND idExamenReserva=:idExamenReserva" +
            " AND rut=:rut " +
            " AND respondida = 0" +
            " AND idSistema=:idSistema" +
            " ORDER BY RANDOM()")
    fun getPreguntasPorExamen(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): List<Preguntas?>?

    @Query("SELECT * FROM preguntas " +
            " WHERE idExamen =:idExamen " +
            " AND idReserva=:idReserva " +
            " AND idExamenReserva=:idExamenReserva" +
            " AND rut=:rut " +
            " AND respondida = 0" +
            " AND idSistema=:idSistema" +
            " ORDER BY orden asc")
    fun getPreguntasPorEncuesta(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): List<Preguntas?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertar(pregunta: Preguntas?)

    @Query("UPDATE preguntas SET  respondida =:manual " +
            " , comentario=:comentario" +
            " WHERE idPregunta =:idPregunta" +
            " AND idExamen=:idExamen" +
            " AND idReserva=:idReserva" +
            " AND rut=:rut" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idSistema=:idSistema")
    fun setPreguntaRespondida(idSistema: String?, idExamenReserva: Int, idPregunta: Int, idExamen: Int, idReserva: Int, rut: String?, manual: Int, comentario: String?)

    @Query("SELECT count(*) FROM preguntas " +
            "WHERE idExamen =:idExamen" +
            " AND idReserva=:idReserva" +
            " AND rut=:rut" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idSistema=:idSistema")
    fun getCountPreguntas(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): Int

    @Query("SELECT sum(CASE WHEN r.escorrecta = 1 AND  r.selecciono = 1 THEN 1 ELSE 0 END) FROM preguntas " +
            " AS p INNER JOIN respuestas AS r ON r.idPregunta = p.idPregunta AND r.idExamen = p.idExamen AND r.idReserva = p.idReserva AND r.rut = p.rut" +
            " WHERE p.idExamen =:idExamen" +
            " AND p.idReserva=:idReserva" +
            " AND p.rut=:rut" +
            " AND p.idExamenReserva=:idExamenReserva" +
            " AND p.idSistema=:idSistema")
    fun getCountPreguntasAcertadas(idSistema: String?, idExamenReserva: Int, idExamen: Int, idReserva: Int, rut: String?): Int

    @Query(" UPDATE preguntas SET pregunta =:pregunta " +
            ", orden =:orden " +
            ", respOrdenadas=:respOrdenadas" +
            " WHERE idPregunta =:idPregunta " +
            " AND idExamen=:idExamen" +
            " AND idReserva=:idReserva" +
            " AND rut=:rut" +
            " AND idExamenReserva=:idExamenReserva" +
            " AND idSistema=:idSistema")
    fun updatePregunta(idSistema: String?, idExamenReserva: Int, idPregunta: Int, idExamen: Int, idReserva: Int, rut: String?, pregunta: String?, orden: Int, respOrdenadas: Boolean)

    @Update
    fun update(pregunta: Preguntas?)

    @Delete
    fun delete(pregunta: Preguntas?)

    @Delete
    fun delete(vararg pregunta: Preguntas?)

    @Query("DELETE FROM Preguntas")
    fun clean()
}