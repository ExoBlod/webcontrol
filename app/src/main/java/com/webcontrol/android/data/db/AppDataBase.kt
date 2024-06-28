package com.webcontrol.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.webcontrol.android.data.db.dao.*
import com.webcontrol.android.data.db.entity.*
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2

@Database(
    entities = [
        Examenes::class,
        Preguntas::class,
        Respuestas::class,
        Message::class,
        MessageHistory::class,
        Cursos::class,
        CheckListTest::class,
        CheckListTest_Detalle::class,
        Usuarios::class,
        Preacceso::class,
        PreaccesoDetalle::class,
        Geofencing::class,
        PuntoMarcacion::class,
        ChecklistGroups::class,
        Encuestas::class,
        ReservaBus::class,
        ReservaCurso::class,
        ReservaBus2::class,
        AnswerQuestion::class,
        InspeccionVehicular::class,
        NewCheckListGroups::class,
        NewCheckListQuestions::class,
        NewCheckListHistorys::class,
        WorkerSignatures::class,
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun cursosDao(): CursosDao
    abstract fun examenesDao(): ExamenesDao
    abstract fun geofencingDao(): GeofencingDao
    abstract fun puntoMarcacionDao(): PuntoMarcacionDao
    abstract fun preguntasDao(): PreguntasDao
    abstract fun respuestasDao(): RespuestasDao
    abstract fun messageDao(): MessageDao
    abstract fun messageHistoryDao(): MessageHistoryDao
    abstract fun usuariosDao(): UsuariosDao
    abstract fun checkListDao(): CheckListDao
    abstract fun encuestasDao(): EncuestasDao
    abstract fun preaccesoDetalleDao(): PreaccesoDetalleDao
    abstract fun preaccesoDao(): PreaccesoDao
    abstract fun checklistGroupsDao(): ChecklistGroupsDao
    abstract fun reservaBusDao(): ReservaBusDao
    abstract fun reservaCursoDao(): ReservaCursoDao
    abstract fun reservaBus2Dao(): ReservaBus2Dao
    abstract fun checkListBambaDao():CheckListBambasDao

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