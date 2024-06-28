package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.model.WorkerPase
import com.webcontrol.android.data.network.ApiResponseAnglo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CheckListTestViewModel : ViewModel() {
    var checkListResultDDS = MutableLiveData<List<CheckListTest>>()
    var checkListResult = MutableLiveData<CheckListTest>()
    var checkListError = MutableLiveData<String?>()
    var checkListLoader = MutableLiveData<Boolean>()
    var disposableObserverCheckList: DisposableObserver<CheckListTest>? = null
    var disposableObserverCheckListDDS: DisposableObserver<List<CheckListTest>>? = null
    var checkListEnvioResult = MutableLiveData<Boolean>()
    var checkListEnvioError = MutableLiveData<String?>()
    var checkListEnvioLoader = MutableLiveData<Boolean>()
    var disposableObserverCheckListEnvio: DisposableObserver<Boolean>? = null
    var disposableObserverCheckListEnvioCdl: DisposableObserver<ApiResponseAnglo<Any>>? = null
    var blockPassError = MutableLiveData<String?>()
    var blockPassLoader = MutableLiveData<Boolean>()
    var disposableObserverBlockPass: DisposableObserver<Boolean>? = null
    var CdlResponse = MutableLiveData<List<String>>()
    var flagCdl = MutableLiveData<Boolean>()
    var msgCdl = MutableLiveData<String>()
    fun getCheckListEnvioResult(): LiveData<Boolean> {
        return checkListEnvioResult
    }

    fun getCheckListEnvioError(): LiveData<String?> {
        return checkListEnvioError
    }

    val blockPassResult: LiveData<Boolean>
        get() = checkListEnvioResult

    fun getBlockPassError(): LiveData<String?> {
        return blockPassError
    }

    fun getCheckListResult(): LiveData<CheckListTest> {
        return checkListResult
    }

    fun getCheckListError(): LiveData<String?> {
        return checkListError
    }

    fun blockPass(workerPase: WorkerPase?) {
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
            .subscribe(disposableObserverBlockPass!!)
    }

    fun getCheckListTest(tipo: String?, id: String?, idCompany: String? = "") {
        disposableObserverCheckList = object : DisposableObserver<CheckListTest>() {
            override fun onNext(checkListTests: CheckListTest) {
                checkListResult.postValue(checkListTests)
                checkListLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                checkListError.postValue(e.message)
                checkListLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getCheckListTestByTipo(tipo!!, id!!, idCompany)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverCheckList!!)
    }

    fun sendCheckListTestCOV(checkListTest: CheckListTest?, idCompany: String? = "") {
        disposableObserverCheckListEnvioCdl = object : DisposableObserver<ApiResponseAnglo<Any>>() {
            override fun onComplete() {}
            override fun onNext(t: ApiResponseAnglo<Any>) {
                val results = listOf<String>(t.data.toString(), t.message)
                checkListEnvioResult.postValue(t.isSuccess)
                checkListEnvioLoader.postValue(false)
                CdlResponse.postValue(results)
            }

            override fun onError(e: Throwable) {
                checkListEnvioError.postValue(e.message)
                checkListEnvioLoader.postValue(false)
            }
        }
        RepositoryCheckList().sendCheckListTestCOV(checkListTest!!, idCompany)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverCheckListEnvioCdl!!)
    }

    fun sendCheckListTest(checkListTest: CheckListTest?) {
        disposableObserverCheckListEnvio = object : DisposableObserver<Boolean>() {
            override fun onNext(checkListTest: Boolean) {
                checkListEnvioResult.postValue(checkListTest)
                checkListEnvioLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                checkListEnvioError.postValue(e.message)
                checkListEnvioLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().sendCheckListTest(checkListTest!!)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverCheckListEnvio!!)
    }

    fun sendCheckListTestKrs(checkListTest: CheckListTest?) {
        disposableObserverCheckListEnvio = object : DisposableObserver<Boolean>() {
            override fun onNext(checkListTest: Boolean) {
                checkListEnvioResult.postValue(checkListTest)
                checkListEnvioLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                checkListEnvioError.postValue(e.message)
                checkListEnvioLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().sendCheckListTestKrs(checkListTest!!)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(10, TimeUnit.SECONDS)
            .subscribe(disposableObserverCheckListEnvio!!)
    }


}