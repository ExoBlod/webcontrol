package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.ReservaBus
import com.webcontrol.android.data.db.repositories.RepositoryReservasBus
import io.reactivex.observers.DisposableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HistoricoReservasBusViewModel : ViewModel() {
    var reservasResult = MutableLiveData<List<ReservaBus>>()
    var reservasError = MutableLiveData<String?>()
    var reservasLoader = MutableLiveData<Boolean>()
    var reservasSyncResult = MutableLiveData<List<ReservaBus>>()
    var reservasSyncError = MutableLiveData<String?>()
    var reservasSyncLoader = MutableLiveData<Boolean>()
    var disposableObserverReservasList: DisposableObserver<List<ReservaBus>>? = null
    var disposableObserverReservasListSync: DisposableObserver<List<ReservaBus>>? = null

    fun getReservasResult(): LiveData<List<ReservaBus>> {
        return reservasResult
    }

    fun getReservasSyncResult(): LiveData<List<ReservaBus>> {
        return reservasSyncResult
    }

    fun getReservasList(date: String?) {
        disposableObserverReservasList = object : DisposableObserver<List<ReservaBus>>() {
            override fun onNext(reservasList: List<ReservaBus>) {
                reservasResult.postValue(reservasList)
                reservasLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                reservasError.postValue(e.message)
                reservasLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryReservasBus().getReservasDisponibles(date!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverReservasList!!)
    }

    fun getReservasListByRut(workerId: String, date: String) {
        disposableObserverReservasListSync = object : DisposableObserver<List<ReservaBus>>() {
            override fun onNext(reservasList: List<ReservaBus>) {
                reservasSyncResult.postValue(reservasList)
                reservasSyncLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                reservasSyncError.postValue(e.message)
                reservasSyncLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        val lastSyncId = RepositoryReservasBus().getMaxSyncId()

        RepositoryReservasBus().getReservasBusByRut(workerId, date, lastSyncId)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverReservasListSync!!)
    }
}