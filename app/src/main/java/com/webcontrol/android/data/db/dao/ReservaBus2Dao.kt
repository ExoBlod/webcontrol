package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import io.reactivex.Single


@Dao
interface ReservaBus2Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReservas(reservaBus: ReservaBus2?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReservas(reservasBus2: List<ReservaBus2?>?)

    @Query("Select * from reserva_bus2 where worker_id = :workerId order by date_reserve desc, time_reserve desc limit 20")
    fun getAllReserves(workerId: String): Single<List<ReservaBus2>>

    @Query("DELETE FROM reserva_bus2")
    fun clean()

    @Query("Select * from reserva_bus2 where code_prog = :codeProd and worker_id=:workerId and status_reserve = 'SI' limit 1")
    fun existReserveBus(codeProd: Long, workerId: String): ReservaBus2?

    @Query("SELECT IFNULL(MAX(sync_id), 0) FROM reserva_bus2")
    fun getMaxSyncId(): Int




}