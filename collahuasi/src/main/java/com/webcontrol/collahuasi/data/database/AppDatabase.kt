package com.webcontrol.collahuasi.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webcontrol.collahuasi.data.database.dao.AttendanceDao
import com.webcontrol.collahuasi.data.database.entity.AttendanceEntity

@Database(entities = [AttendanceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
}