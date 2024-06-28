package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Respuestas
import com.webcontrol.android.data.model.RespuestasHist

@Dao
interface RespuestasDao {
    @Query(
        "SELECT * FROm respuestas " +
                " WHERE  idRespuesta=:idRespuesta" +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idPregunta=:idPregunta" +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut" +
                " AND idSistema=:idSistema"
    )
    fun getOne(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?,
        idRespuesta: Int
    ): Respuestas?

    @Query(
        "SELECT * FROM respuestas " +
                " WHERE idPregunta =:idPregunta" +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut" +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idSistema=:idSistema" +
                " ORDER BY RANDOM() "
    )
    fun getRespuestasByPregunta(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?
    ): List<Respuestas?>?

    @Query(
        "SELECT * FROM respuestas " +
                " WHERE idPregunta =:idPregunta" +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut " +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idSistema=:idSistema" +
                " order by orden asc"
    )
    fun getRespuestasOrdenadasByPregunta(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?
    ): List<Respuestas?>?

    @Query(
        "SELECT * FROM respuestas " +
                " WHERE idPregunta =:idPregunta" +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut" +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idSistema=:idSistema" +
                " ORDER BY orden asc"
    )
    fun getAllByRespuestaByPregunta(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?
    ): List<Respuestas?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertar(respuesta: Respuestas?)

    @Query(
        "UPDATE respuestas SET respuesta =:respuesta " +
                "                   , orden =:orden   " +
                "WHERE idRespuesta=:idRespuesta " +
                " AND idPregunta =:idPregunta" +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut" +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idSistema=:idSistema"
    )
    fun updateRespuesta(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?,
        idRespuesta: Int,
        respuesta: String?,
        orden: Int
    )

    @Query(
        "UPDATE respuestas SET selecciono = 1   " +
                "WHERE idPregunta =:idPregunta " +
                " AND idExamen=:idExamen" +
                " AND idReserva=:idReserva" +
                " AND rut=:rut" +
                " AND idRespuesta=:idRespuesta " +
                " AND idExamenReserva=:idExamenReserva" +
                " AND idSistema=:idSistema"
    )
    fun setRespuestaRespondida(
        idSistema: String?,
        idExamenReserva: Int,
        idPregunta: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?,
        idRespuesta: Int
    )

    @Query(
        "SELECT p.idExamenReserva,p.idPregunta , p.rut , p.idExamen , p.idReserva , p.respondida , r.idRespuesta , p.respondida ,p.comentario as observacion" +
                "  FROM preguntas AS p " +
                " LEFT JOIN  respuestas  AS r " +
                " ON r.idReserva = p.idReserva AND r.idExamen = p.idExamen AND r.rut = p.rut AND r.idPregunta = p.idPregunta AND r.selecciono = 1 " +
                " WHERE p.idExamen=:idExamen" +
                " AND p.idReserva=:idReserva" +
                " AND p.rut=:rut" +
                " AND p.idExamenReserva=:idExamenReserva" +
                " AND p.idSistema=:idSistema"
    )

    fun getAllRespuestasMarcadas(
        idSistema: String?,
        idExamenReserva: Int,
        idExamen: Int,
        idReserva: Int,
        rut: String?
    ): List<RespuestasHist>?

    @Update
    fun update(respuesta: Respuestas?)

    @Delete
    fun delete(respuesta: Respuestas?)

    @Delete
    fun delete(vararg respuesta: Respuestas?)

    @Query("DELETE FROM Respuestas")
    fun clean()
}