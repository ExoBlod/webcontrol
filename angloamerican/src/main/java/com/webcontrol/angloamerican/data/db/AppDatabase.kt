package com.webcontrol.angloamerican.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webcontrol.angloamerican.data.db.dao.CheckListDao
import com.webcontrol.angloamerican.data.db.dao.ChecklistGroupsDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoDetalleMinaDao
import com.webcontrol.angloamerican.data.db.dao.PreaccesoMinaDao
import com.webcontrol.angloamerican.data.db.entity.*

@Database(entities = [
    PreaccesoMina::class,
    PreaccesoDetalleMina::class,
    CheckListTest::class,
    CheckListTest_Detalle::class,
    ChecklistGroups::class,
], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun preAccesoMina(): PreaccesoMinaDao
    abstract fun preAccesodetalleMina(): PreaccesoDetalleMinaDao
    abstract fun checkListDao(): CheckListDao
    abstract fun checkListGroupsDao(): ChecklistGroupsDao

    companion object {
        var DB_NAME = "angloamerican.sqlite"
    }
}