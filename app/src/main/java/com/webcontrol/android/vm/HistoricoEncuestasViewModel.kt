package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.data.db.repositories.RepositoryEncuestas
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HistoricoEncuestasViewModel : ViewModel() {
    var encuestasResult = MutableLiveData<List<Encuestas>>()
    var encuestasError = MutableLiveData<String?>()
    var encuestasLoader = MutableLiveData<Boolean>()
    var disposableObserverCheckList: DisposableObserver<List<Encuestas>>? = null
    fun getEncuestasResult(): LiveData<List<Encuestas>> {
        return encuestasResult
    }

    fun getCheckList(tipo: String?) {
        disposableObserverCheckList = object : DisposableObserver<List<Encuestas>>() {
            override fun onNext(checkListTests: List<Encuestas>) {
                encuestasResult.postValue(checkListTests)
                encuestasLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                encuestasError.postValue(e.message)
                encuestasLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryEncuestas().getEncuestasDBByTipo(tipo!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverCheckList!!)
    }
}