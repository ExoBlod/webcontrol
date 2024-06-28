package com.webcontrol.android.data.db.repositories

import com.webcontrol.android.App
import com.webcontrol.android.data.db.entity.ReservaBus
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.android.util.RestClient
import io.reactivex.Observable

class RepositoryReservasBus {
    fun getReservasDisponibles(date: String): Observable<List<ReservaBus>> {
        return App.db.reservaBusDao().selectReservasDisponibles(date).toObservable()
    }

    fun getMaxSyncId(): Int {
        return App.db.reservaBusDao().getMaxSyncId()
    }

    fun getReservasBusByRut(workerId: String, date: String, syncId: Int): Observable<List<ReservaBus>> {
        return RestClient.buildAngloRx().getReservasBusByRut(RequestReservaBus(workerId, date, syncId))
                .map { it.data }
                .doOnNext {
                    App.db.reservaBusDao().insertAllReservas(it)
                }
    }
}