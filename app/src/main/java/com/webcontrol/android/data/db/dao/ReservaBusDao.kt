package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.ReservaBus
import io.reactivex.Single

@Dao
interface ReservaBusDao {
    @Query("SELECT * FROM Reserva_Bus WHERE utilizo = 'NO' AND estado = 'SI' AND fecha >= :date ORDER BY fecha ASC, hora ASC LIMIT 10")
    fun selectReservasDisponibles(date: String?): Single<List<ReservaBus>>

    @Query("SELECT * FROM Reserva_Bus WHERE prog_id = :progId AND worker_id = :workerId")
    fun selectReservaByProgIdAndWorkerId(progId: Int, workerId: String): ReservaBus?

    @Query("SELECT IFNULL(MAX(sync_id), 0) FROM Reserva_Bus")
    fun getMaxSyncId(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReservas(reservaBus: ReservaBus?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReservas(reservasBus: List<ReservaBus?>?)

    @Delete
    fun deleteReservas(reservaBus: ReservaBus?)

    @Query("DELETE FROM Reserva_Bus")
    fun clean()
}