package com.webcontrol.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webcontrol.android.data.db.entity.Usuarios

@Dao
interface UsuariosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(usuario: Usuarios?)

    @Query("delete from usuarios")
    fun clean()
}