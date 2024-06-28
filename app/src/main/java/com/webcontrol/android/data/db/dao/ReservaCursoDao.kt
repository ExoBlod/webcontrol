package com.webcontrol.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webcontrol.android.data.db.entity.ReservaCurso
import io.reactivex.Single

@Dao
interface ReservaCursoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReservas(reservasCurso: List<ReservaCurso?>?)

    @Query("SELECT * FROM Reserva_Curso  ORDER BY status_reserve DESC, date_reserve DESC, time_reserve DESC LIMIT 30")
    fun getAllReserves(): Single<List<ReservaCurso>>


    @Query("SELECT * FROM Reserva_Curso WHERE code_reserve = :codeReserve limit 1")
    fun getOneByReserve(codeReserve: Int): ReservaCurso

    @Query("SELECT * FROM Reserva_Curso WHERE code_reserve = :codeReserve and status_reserve = 'R' limit 1")
    fun getOneByReserveMade(codeReserve: Int): ReservaCurso




}