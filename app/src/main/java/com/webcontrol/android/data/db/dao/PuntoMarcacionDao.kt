package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.PuntoMarcacion

@Dao
interface PuntoMarcacionDao {
    @get:Query("select * from puntos_marcacion")
    val all: List<PuntoMarcacion?>?

    @Query("select * from puntos_marcacion where codigo=:id")
    fun getOneById(id: Int): PuntoMarcacion?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(punto: PuntoMarcacion?)

    @Update
    fun update(punto: PuntoMarcacion?)

    @Delete
    fun delete(punto: PuntoMarcacion?)

    @Delete
    fun delete(vararg punto: PuntoMarcacion?)

    @Query("delete from puntos_marcacion")
    fun clean()
}