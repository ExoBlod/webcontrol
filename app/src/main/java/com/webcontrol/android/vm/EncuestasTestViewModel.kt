package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.db.repositories.RepositoryEncuestas
import com.webcontrol.android.data.model.ResponseExam
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EncuestasTestViewModel : ViewModel() {
    var encuestasResult = MutableLiveData<ResponseExam>()
    var encuestasError = MutableLiveData<String?>()
    var encuestasLoader = MutableLiveData<Boolean>()
    var encuestasEnvioResult = MutableLiveData<Boolean>()
    var encuestasEnvioError = MutableLiveData<String?>()
    var encuestasEnvioLoader = MutableLiveData<Boolean>()
    var disposableObserverEncuestas: DisposableObserver<ResponseExam>? = null
    var disposableObserverEncuestasEnvio: DisposableObserver<Boolean>? = null

    fun getEncuestasResult(): LiveData<ResponseExam> {
        return encuestasResult
    }

    fun getEncuestasEnvioResult(): LiveData<Boolean> {
        return encuestasEnvioResult
    }

    fun getEncuestaTest(tipo: String?, id: String?,rut: String) {
        disposableObserverEncuestas = object : DisposableObserver<ResponseExam>() {
            override fun onNext(checkListTests: ResponseExam) {
                encuestasResult.postValue(checkListTests)
                encuestasLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                encuestasError.postValue(e.message)
                encuestasLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryEncuestas().getEncuestasTestByTipo(tipo!!, id!!,rut)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverEncuestas!!)
    }

    fun sendEncuestaTest(responseExam: ResponseExam?) {
        disposableObserverEncuestasEnvio = object : DisposableObserver<Boolean>() {
            override fun onNext(result: Boolean) {
                encuestasEnvioResult.postValue(result)
                encuestasEnvioLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                encuestasEnvioError.postValue(e.message)
                encuestasEnvioLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryEncuestas().sendEncuestaTest(responseExam!!)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverEncuestasEnvio!!)
    }
}