package com.webcontrol.android.ui.covid.antapaccay.initialdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.model.DatosInicialesWorker
import com.webcontrol.android.data.model.WorkerPase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DatosInicialesViewModel  @Inject constructor() : ViewModel() {

    var dataInicialResult = MutableLiveData<DatosInicialesWorker>()
    var dataInicialError = MutableLiveData<String>()
    var disposableObserverDataInicial: DisposableObserver<DatosInicialesWorker>? = null

    var dataUpdateResult = MutableLiveData<Boolean>()
    var dataUpdateError = MutableLiveData<String>()
    var disposableObserverDataUpdate: DisposableObserver<Boolean>? = null
    var dataUpdateLoader = MutableLiveData<Boolean>()

    var blockPassError = MutableLiveData<String>()
    var blockPassResult = MutableLiveData<Boolean>()
    var blockPassLoader = MutableLiveData<Boolean>()
    var disposableObserverBlockPass: DisposableObserver<Boolean>? = null

    fun getDataUpdateResult(): LiveData<Boolean> {return dataUpdateResult}

    fun getDataUpdateError(): LiveData<String> { return dataUpdateError }

    fun getBlockPassResult(): LiveData<Boolean?> { return blockPassResult }

    fun getBlockPassError(): LiveData<String?> { return blockPassError }

    fun getDataInicialResult(): LiveData<DatosInicialesWorker> { return dataInicialResult }

    fun getDataInicialError(): LiveData<String> { return dataInicialError }

    fun getDataInicial(rut: String?) {
        disposableObserverDataInicial = object : DisposableObserver<DatosInicialesWorker>() {
            override fun onNext(t: DatosInicialesWorker) { dataInicialResult.postValue(t) }
            override fun onError(e: Throwable) { dataInicialError.postValue(e.message) }
            override fun onComplete() {}

        }
        RepositoryCheckList().getDatosInicialesAnta(rut!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverDataInicial as DisposableObserver<DatosInicialesWorker>)
    }

    fun updateDataInicial(data: DatosInicialesWorker) {
        disposableObserverDataUpdate = object : DisposableObserver<Boolean>() {
            override fun onComplete() {}
            override fun onNext(t: Boolean) {
                dataUpdateResult.postValue(t)
                dataUpdateLoader.postValue(false)
            }
            override fun onError(e: Throwable) {
                dataUpdateError.postValue(e.message)
                dataUpdateLoader.postValue(false)
            }
        }
        RepositoryCheckList().updateDatosInicialesAnta(data!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverDataUpdate as DisposableObserver<Boolean>)
    }

    /*fun blockPass(workerPase: WorkerPase?) {
        disposableObserverBlockPass = object : DisposableObserver<Boolean>() {
            override fun onNext(aBoolean: Boolean) {}
            override fun onError(e: Throwable) {
                blockPassError.postValue(e.message)
                blockPassLoader.postValue(false)
            }
            override fun onComplete() {}
        }
        RepositoryCheckList().blockWorkerPass(workerPase!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverBlockPass as DisposableObserver<Boolean>)
    }*/
}