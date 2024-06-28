package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.Preacceso
import kotlinx.coroutines.flow.Flow

@Dao
interface PreaccesoDao {
    @Query("select * from preacceso order by id desc")
    fun all(): List<Preacceso>

    @Query("select * from preacceso order by id desc limit 1")
    fun getLastInserted(): Flow<Preacceso?>

    @Query("select * from preacceso where id = :id")
    fun getOne(id: Int): Preacceso?

    @Query("select id from preacceso order by id desc limit 1")
    fun lastInsertedId(): Int

    @Insert
    suspend fun insert(preaccess: Preacceso): Long

    @Update
    fun update(preacceso: Preacceso?)

    @Delete
    suspend fun delete(preacceso: Preacceso?)

    @Query("update preacceso set estado = 'S' where id = :id")
    suspend fun updateStatus(id: Int)

    @Query("delete from preacceso")
    fun clean()
}