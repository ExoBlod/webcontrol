package com.webcontrol.android.data.db.repositories

import com.webcontrol.android.App
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.model.RequestReservaCurso
import com.webcontrol.android.util.Constants.SIZE_PAGE_HISTORY_COURSE
import com.webcontrol.android.util.RestClient
import io.reactivex.Observable


class RepositoryReservaCurso {

    fun getAllReserves(): Observable<List<ReservaCurso>> {
        return App.db.reservaCursoDao().getAllReserves().toObservable()
    }

    fun getHistoryReservaCursos(workerId: String): Observable<List<ReservaCurso>> {
        return RestClient.buildAngloRx().getHistoryReserves(RequestReservaCurso(SIZE_PAGE_HISTORY_COURSE, workerId))
            .map { it.data }
            .doOnNext {
                App.db.reservaCursoDao().insertAllReservas(it)
            }
    }
}