package com.webcontrol.android.angloamerican.data.repositories

import com.webcontrol.android.App
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.util.RestClient
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.angloamerican.data.ResponseSeatBus
import io.reactivex.Observable

class ReservaBusRepository(private val api: RestInterfaceAnglo){
    suspend fun getDivitions(): ApiResponseAnglo<List<Division>> {
        return api.getDivisions()
    }

    suspend fun getSourcesReservaBus(divition: String): ApiResponseAnglo<List<SourceReservaBus>> {
        return api.getSources(RequestReservaBus(divisionId = divition))
    }

    suspend fun getDestiniesReservaBus(divition: String): ApiResponseAnglo<List<DestinyReservaBus>> {
        return api.getDestinies(RequestReservaBus(divisionId = divition))
    }




    fun getHistoryReserves(request: RequestReservaBus): Observable<List<ReservaBus2>> {
        return RestClient.buildAngloRx().getHistoryReservesBus(request)
            .map { it.data }
            .doOnNext {
                App.db.reservaBus2Dao().insertAllReservas(it)
            }
    }



    suspend fun getSeatsBus(request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseSeatBus>>{
        return api.getSeatsBus(request)
    }

    fun getAllReserves(workerId: String): Observable<List<ReservaBus2>> {
        return App.db.reservaBus2Dao().getAllReserves(workerId).toObservable()
    }


    suspend fun insertReserves(listReserves: List<ReservaBus2>) {
        App.db.reservaBus2Dao().insertAllReservas(listReserves)
    }

    suspend fun reserveBus(request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseReservaBus>>{
        return api.insertReserveBus(request)
    }

    suspend fun cancelReserveBus(request: RequestReservaBus): ApiResponseAnglo<MutableList<ResponseReservaBus>>{
        return api.cancelReserveBus(request)
    }

    fun getMaxSyncId(): Int {
        return App.db.reservaBus2Dao().getMaxSyncId()
    }

    suspend fun getBusesAvailable(request: RequestReservaBus): ApiResponseAnglo<List<ResponseReservaBus>>{
        return api.getBusesAvailable(request)
    }

    suspend fun existReserveBus(codeProg: Long, workerId: String): ReservaBus2? {
        return App.db.reservaBus2Dao().existReserveBus(codeProg, workerId)
    }

    suspend fun getWorkerInfo(request: WorkerRequest): ApiResponseAnglo<WorkerAnglo?>{
        return api.getWorker(request)
    }



}