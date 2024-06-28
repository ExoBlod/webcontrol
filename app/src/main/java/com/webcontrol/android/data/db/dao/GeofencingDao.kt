package com.webcontrol.android.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.webcontrol.android.data.db.entity.Geofencing

@Dao
interface GeofencingDao {

    @Query("select * from geofencing")
    fun getAll(): LiveData<List<Geofencing>>

    @Query("select * from geofencing")
    fun getAllList(): List<Geofencing>

    @Query("select * from geofencing where status='P'")
    fun getAllPendientesList(): List<Geofencing>

    @Query("select * from geofencing order by id desc limit 1")
    fun getLast(): Geofencing

    @Insert
    fun insert(geofencing: Geofencing)

    @Update
    fun update(geofencing: Geofencing)

    @Delete
    fun delete(geofencing: Geofencing)

    @Query("delete from geofencing")
    fun clean()
}
