package com.webcontrol.angloamerican.ui.checklistpreuso.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.dao.CheckListPreUsoDao
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.*
import com.webcontrol.angloamerican.ui.checklistpreuso.data.model.InspectionHistory


@Database(
    entities = [
        InspectionHistory::class,
        WorkerSignature::class,
        NewCheckListQuestions::class,
        NewCheckListGroups::class,
        InspeccionVehicular::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun checkListPreUso():CheckListPreUsoDao

    companion object {
        var DB_NAME = "webcontrol.sqlite"
        private var instance: AppDataBase? = null

        @Synchronized
        fun getInstance(context: Context?): AppDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!,
                    AppDataBase::class.java, DB_NAME
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }

    }
}