package com.webcontrol.android.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.webcontrol.android.data.db.entity.Empresas
import com.webcontrol.android.data.db.entity.Message
import androidx.room.OnConflictStrategy

@Dao
interface MessageDao {
    @Query("SELECT MAX(idsync) FROM message WHERE rut=:rut")
    fun getMaxIdSync(rut: String?): Long

    @Query("select * from message where id=:messageId")
    fun getOne(messageId: String?): Message?

    @Query(
        "select * from message " +
                " WHERE rut=:rut " +
                "order by hora,fecha desc"
    )
    fun getAll(rut: String?): List<Message?>?

    @Query(
        "select * from message where estado<>3 " +
                " AND rut=:rut " +
                "order by id desc"
    )
    fun getAllVisible(rut: String?): LiveData<List<Message>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: Message?)

    @Query(
        "UPDATE message SET estado=:estado" +
                ", isImportant=:importante " +
                " , idSync=:idSync" +
                " WHERE id=:id"
    )
    fun updateMessage(id: Int, estado: Int, importante: Boolean, idSync: Long)

    @Update
    fun update(message: Message?)

    @Delete
    fun delete(message: Message?)

    @Delete
    fun delete(vararg messages: Message?)

    @Query("delete from message")
    fun clean()

    @Query("update message set estadoSincronizado = :sincronizado where id = :id")
    fun updateEstadoSync(id: Int, sincronizado: Boolean)

    @Query("update message set importanteSincronizado = :sincronizado where id = :id")
    fun updateImportanteSync(id: Int, sincronizado: Boolean)

    @Query("select * from message where estadoSincronizado = 0")
    fun estadosNoSincronizados(): List<Message?>?

    @Query("select * from message where importanteSincronizado = 0")
    fun importantesNoSincronizados(): List<Message?>?

    @Query(
        """
        SELECT 
            COUNT(*) AS numRegistros,
            CASE WHEN (MANDANTE IS NULL OR MANDANTE = '') THEN ''  ELSE  MANDANTE END as RutEmpresa,
            NOMEMPRESA as nomEmpresa 
        FROM message
            WHERE rut= :rut
        GROUP BY MANDANTE,NOMEMPRESA
        """)
    fun getDistinctEmpresas(rut: String?): List<Empresas>?

    @Query(
        "select * from message " +
                " WHERE rut=:rut " +
                " AND MANDANTE=:IdEmpresa" +
                " order by id desc"
    )
    fun getAllByEmpresa(rut: String?, IdEmpresa: String?): LiveData<List<Message>>
}