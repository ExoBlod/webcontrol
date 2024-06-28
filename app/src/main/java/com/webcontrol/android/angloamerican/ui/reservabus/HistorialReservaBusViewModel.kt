package com.webcontrol.android.angloamerican.ui.reservabus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistorialReservaBusViewModel @Inject constructor(
    private val repository: ReservaBusRepository
) : ViewModel() {
    val historyState: MutableLiveData<Resource<List<ReservaBus2>>> =
        MutableLiveData<Resource<List<ReservaBus2>>>()

    val listReserveState: MutableLiveData<Resource<List<ReservaBus2>>> =
        MutableLiveData<Resource<List<ReservaBus2>>>()

    var disposableObserverReservasList: DisposableObserver<List<ReservaBus2>>? = null
    var disposableObserverReservasListSync: DisposableObserver<List<ReservaBus2>>? = null


    fun getListReserves(workerId: String) {
        disposableObserverReservasList = object : DisposableObserver<List<ReservaBus2>>() {
            override fun onNext(reservasList: List<ReservaBus2>) {
                listReserveState.postValue(Resource.Success(reservasList))
            }

            override fun onError(e: Throwable) {
                listReserveState.postValue(Resource.Error(e.message.toString()))
            }

            override fun onComplete() {}
        }
        repository.getAllReserves(workerId)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverReservasList!!)
    }

    fun getHistoryReserves(rut: String) {
        disposableObserverReservasListSync = object : DisposableObserver<List<ReservaBus2>>() {
            override fun onNext(reservasList: List<ReservaBus2>) {
                historyState.postValue(Resource.Success(reservasList))
            }

            override fun onError(e: Throwable) {
                historyState.postValue(Resource.Error(e.message.toString()))
            }

            override fun onComplete() {}
        }

        val lastSyncId = repository.getMaxSyncId()
        val request = RequestReservaBus(
            rut = rut,
            syncId = lastSyncId,
            pageSize = 30
        )

        repository.getHistoryReserves(request)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverReservasListSync!!)
    }


}