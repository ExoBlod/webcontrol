package com.webcontrol.angloamerican.data.db.dao

import androidx.room.*
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import kotlinx.coroutines.flow.Flow

@Dao
interface PreaccesoMinaDao {
    @Query("select * from preacceso_mina order by id desc")
    suspend fun all(): List<PreaccesoMina>

    @Query("select * from preacceso_mina order by id desc limit 1")
    suspend fun getLastInserted(): PreaccesoMina?

    @Query("select * from preacceso_mina where id = :id")
    suspend fun getOne(id: Int): PreaccesoMina?

    @Query("select id from preacceso_mina order by id desc limit 1")
    suspend fun lastInsertedId(): Int

    /*@Query("select id from preacceso_mina where estado = 'P' order by id desc ")
    suspend fun getAllPending(): List<PreaccesoMina>?*/

    @Insert
    suspend fun insert(preaccess: PreaccesoMina): Long

    @Update
    suspend fun update(preacceso: PreaccesoMina?)

    @Query("delete from preacceso_mina where id = :id")
    suspend fun deletePreaccess(id: Int)

    @Query("update preacceso_mina set estado = 'S' where id = :id")
    suspend fun updateStatus(id: Int)

    @Query("delete from preacceso_mina")
    suspend fun clean()
}