package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.db.repositories.RepositoryReservaCurso
import com.webcontrol.android.data.db.repositories.RepositoryReservasBus
import io.reactivex.observers.DisposableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HistoricoReservaCursoViewModel : ViewModel() {
    var reservasResult = MutableLiveData<List<ReservaCurso>>()
    var reservasError = MutableLiveData<String?>()
    var reservasLoader = MutableLiveData<Boolean>()
    var reservasSyncResult = MutableLiveData<List<ReservaCurso>>()
    var reservasSyncError = MutableLiveData<String?>()
    var reservasSyncLoader = MutableLiveData<Boolean>()
    var disposableObserverReservasList: DisposableObserver<List<ReservaCurso>>? = null
    var disposableObserverReservasListSync: DisposableObserver<List<ReservaCurso>>? = null

    fun getReservasResult(): LiveData<List<ReservaCurso>> {
        return reservasResult
    }

    fun getReservaError(): LiveData<String?> {
        return reservasError
    }

    fun getReservasSyncResult(): LiveData<List<ReservaCurso>> {
        return reservasSyncResult
    }

    fun getHistoryReservas() {
        disposableObserverReservasList = object : DisposableObserver<List<ReservaCurso>>() {
            override fun onNext(reservasList: List<ReservaCurso>) {
                reservasResult.postValue(reservasList)
                reservasLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                reservasError.postValue(e.message)
                reservasLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryReservaCurso().getAllReserves()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverReservasList!!)
    }

    fun getSyncReservasListByRut(workerId: String) {
        disposableObserverReservasListSync = object : DisposableObserver<List<ReservaCurso>>() {
            override fun onNext(reservasList: List<ReservaCurso>) {
                reservasSyncResult.postValue(reservasList)
                reservasSyncLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                reservasSyncError.postValue(e.message)
                reservasSyncLoader.postValue(false)
            }

            override fun onComplete() {}
        }

        RepositoryReservaCurso().getHistoryReservaCursos(workerId)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverReservasListSync!!)
    }
}