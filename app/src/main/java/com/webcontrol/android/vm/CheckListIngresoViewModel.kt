package com.webcontrol.android.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.model.Local
import com.webcontrol.android.data.model.Vehiculo
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CheckListIngresoViewModel : ViewModel() {
    var validarPatenteResult = MutableLiveData<ApiResponseAnglo<Vehiculo>>()
    var validarPatenteError = MutableLiveData<String?>()
    var disposableObserverValidarPatente: DisposableObserver<ApiResponseAnglo<Vehiculo>>? = null

    @JvmField
    var seValidoPatente = MutableLiveData<Boolean>()

    @JvmField
    var getCompanyResult = MutableLiveData<ApiResponseAnglo<WorkerAnglo>>()
    var getCompanyError = MutableLiveData<String?>()
    var disposableObserverCompany: DisposableObserver<ApiResponseAnglo<WorkerAnglo>>? = null
    var getDivisionesResult = MutableLiveData<ApiResponseAnglo<List<Division>>>()
    var getDivisionesError = MutableLiveData<String?>()
    var disposableObservergetDivisiones: DisposableObserver<ApiResponseAnglo<List<Division>>>? = null
    var getLocalesResult = MutableLiveData<ApiResponseAnglo<List<Local>>>()
    var getLocalesError = MutableLiveData<String?>()
    var disposableObservergetLocales: DisposableObserver<ApiResponseAnglo<List<Local>>>? = null
    var getWorkerByDivisionObservableResult = MutableLiveData<ApiResponseAnglo<WorkerAnglo>>()
    var getWorkerByDivisionObservableError = MutableLiveData<String?>()
    var disposableObserverWorkerByDivision: DisposableObserver<ApiResponseAnglo<WorkerAnglo>>? = null
    fun getValidarPatenteResult(): LiveData<ApiResponseAnglo<Vehiculo>> {
        return validarPatenteResult
    }

    fun getValidarPatenteError(): LiveData<String?> {
        return validarPatenteError
    }

    fun getGetDivisionesResult(): LiveData<ApiResponseAnglo<List<Division>>> {
        return getDivisionesResult
    }

    fun getGetDivisionesError(): LiveData<String?> {
        return getDivisionesError
    }

    fun getGetLocalesResult(): LiveData<ApiResponseAnglo<List<Local>>> {
        return getLocalesResult
    }

    fun getGetLocalesError(): LiveData<String?> {
        return getLocalesError
    }

    val workerByDivisionObservableResult: LiveData<ApiResponseAnglo<WorkerAnglo>>
        get() = getWorkerByDivisionObservableResult

    val workerByDivisionObservableError: LiveData<String?>
        get() = getWorkerByDivisionObservableError

    @JvmField
    var txtPatente = MutableLiveData<String?>()

    @JvmField
    var txtPatenteError = MutableLiveData<String>()

    @JvmField
    var ddlDivisionIdSelected = MutableLiveData<String?>()

    @JvmField
    var ddlDivisionError = MutableLiveData<String>()

    @JvmField
    var ddlLocalIdSelected = MutableLiveData<String?>()

    @JvmField
    var ddlLocalError = MutableLiveData<String>()

    @JvmField
    var txtUserNames = MutableLiveData<String>()

    @JvmField
    var txtUserRut = MutableLiveData<String>()
    fun validarPatenteWS(Patente: String?, Division: String?) {
        disposableObserverValidarPatente = object : DisposableObserver<ApiResponseAnglo<Vehiculo>>() {
            override fun onNext(result: ApiResponseAnglo<Vehiculo>) {
                validarPatenteResult.postValue(result)
            }

            override fun onError(e: Throwable) {
                validarPatenteError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().validatePatente(Patente!!, Division!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverValidarPatente!!)
    }

    fun validarPatenteWSKrs(Patente: String?, Division: String?) {
        disposableObserverValidarPatente = object : DisposableObserver<ApiResponseAnglo<Vehiculo>>() {
            override fun onNext(result: ApiResponseAnglo<Vehiculo>) {
                validarPatenteResult.postValue(result)
            }

            override fun onError(e: Throwable) {
                validarPatenteError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().validatePatenteKrs(Patente!!, Division!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverValidarPatente!!)
    }

    fun validarConductor(rut: String?, divisionId: String?) {
        disposableObserverWorkerByDivision = object : DisposableObserver<ApiResponseAnglo<WorkerAnglo>>() {
            override fun onNext(workerAngloApiResponseAnglo: ApiResponseAnglo<WorkerAnglo>) {
                getWorkerByDivisionObservableResult.postValue(workerAngloApiResponseAnglo)
            }

            override fun onError(e: Throwable) {
                getWorkerByDivisionObservableError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getWorkerByDivision(rut!!, divisionId!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserverWorkerByDivision!!)
    }

    fun validarConductorKrs(rut: String?, divisionId: String?) {
        disposableObserverWorkerByDivision = object : DisposableObserver<ApiResponseAnglo<WorkerAnglo>>() {
            override fun onNext(workerAngloApiResponseAnglo: ApiResponseAnglo<WorkerAnglo>) {
                getWorkerByDivisionObservableResult.postValue(workerAngloApiResponseAnglo)
            }

            override fun onError(e: Throwable) {
                getWorkerByDivisionObservableError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getWorkerByDivisionKrs(rut!!, divisionId!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserverWorkerByDivision!!)
    }

    fun getWorkerCompany(rut: String?) {
        disposableObserverCompany = object : DisposableObserver<ApiResponseAnglo<WorkerAnglo>>() {
            override fun onNext(workerAngloApiResponseAnglo: ApiResponseAnglo<WorkerAnglo>) {
                getCompanyResult.postValue(workerAngloApiResponseAnglo)
            }

            override fun onError(e: Throwable) {
                getCompanyError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getWorker(rut!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverCompany!!)
    }

    fun getWorkerCompanyKrs(rut: String?) {
        disposableObserverCompany = object : DisposableObserver<ApiResponseAnglo<WorkerAnglo>>() {
            override fun onNext(workerAngloApiResponseAnglo: ApiResponseAnglo<WorkerAnglo>) {
                getCompanyResult.postValue(workerAngloApiResponseAnglo)
            }

            override fun onError(e: Throwable) {
                getCompanyError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getWorkerKrs(rut!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverCompany!!)
    }

    fun getDivisions() {
        disposableObservergetDivisiones = object : DisposableObserver<ApiResponseAnglo<List<Division>>>() {
            override fun onNext(divisions: ApiResponseAnglo<List<Division>>) {
                getDivisionesResult.postValue(divisions)
            }

            override fun onError(e: Throwable) {
                getDivisionesError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getDivisiones()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObservergetDivisiones!!)
    }

    fun getDivisionsKrs() {
        disposableObservergetDivisiones = object : DisposableObserver<ApiResponseAnglo<List<Division>>>() {
            override fun onNext(divisions: ApiResponseAnglo<List<Division>>) {
                getDivisionesResult.postValue(divisions)
            }

            override fun onError(e: Throwable) {
                getDivisionesError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getDivisionesKrs()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObservergetDivisiones!!)
    }

    fun getLocales(division: String?) {
        disposableObservergetLocales = object : DisposableObserver<ApiResponseAnglo<List<Local>>>() {
            override fun onNext(locals: ApiResponseAnglo<List<Local>>) {
                getLocalesResult.postValue(locals)
            }

            override fun onError(e: Throwable) {
                getLocalesError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getLocales(division!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObservergetLocales!!)
    }

    fun getLocalesKrs(division: String?) {
        disposableObservergetLocales = object : DisposableObserver<ApiResponseAnglo<List<Local>>>() {
            override fun onNext(locals: ApiResponseAnglo<List<Local>>) {
                getLocalesResult.postValue(locals)
            }

            override fun onError(e: Throwable) {
                getLocalesError.postValue(e.message)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getLocalesKrs(division!!)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObservergetLocales!!)
    }

    fun validarPatente() {
        if (txtPatente.value != null) if (!txtPatente.value!!.isEmpty()) validarPatenteWS(txtPatente.value, ddlDivisionIdSelected.value) else txtPatenteError.postValue("Ingrese una patente correcta") else txtPatenteError.postValue("Ingrese una patente correcta")
    }

    fun validarPatenteKrs() {
        if (txtPatente.value != null) if (!txtPatente.value!!.isEmpty()) validarPatenteWSKrs(txtPatente.value, ddlDivisionIdSelected.value) else txtPatenteError.postValue("Ingrese una patente correcta") else txtPatenteError.postValue("Ingrese una patente correcta")
    }

    fun validarDatos(): Boolean {
        var validate = true
        if (txtPatente.value == null || txtPatente.value.equals("", ignoreCase = true)) {
            validate = false
            txtPatenteError.postValue("Ingrese una patente correcta")
        } else if (!seValidoPatente.value!!) {
            validate = false
            txtPatenteError.postValue("Debe Validar la patente")
        }
        if (ddlDivisionIdSelected.value == null || ddlDivisionIdSelected.value.equals("", ignoreCase = true)) {
            validate = false
            ddlDivisionError.postValue("Seleccione División")
        }
        if (ddlLocalIdSelected.value == null || ddlLocalIdSelected.value.equals("", ignoreCase = true)) {
            validate = false
            ddlLocalError.postValue("Seleccione Local")
        }
        return validate
    }
}