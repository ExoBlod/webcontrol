package com.webcontrol.android.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.repositories.RepositoryCheckList
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HistoricoCheckListViewModel : ViewModel() {
    var checkListResult = MutableLiveData<List<CheckListTest>>()
    var checkListError = MutableLiveData<String?>()
    var checkListLoader = MutableLiveData<Boolean>()
    var disposableObserverCheckList: DisposableObserver<List<CheckListTest>>? = null
    var getWorkerObservableResult = MutableLiveData<ApiResponseAnglo<WorkerAnglo>>()
    var getWorkerObservableError = MutableLiveData<String>()
    var cdlResult = MutableLiveData<Int>()
    fun getCheckListResult(): LiveData<List<CheckListTest>> {
        return checkListResult
    }

    fun getCheckListError(): LiveData<String?> {
        return checkListError
    }

    val workerObservableResult: LiveData<ApiResponseAnglo<WorkerAnglo>>
        get() = getWorkerObservableResult

    val workerObservableError: LiveData<String>
        get() = getWorkerObservableError

    fun getCheckList(tipo: String, idCompany: String?="") {
        disposableObserverCheckList = object : DisposableObserver<List<CheckListTest>>() {
            override fun onNext(checkListTests: List<CheckListTest>) {
                checkListResult.postValue(checkListTests)
                checkListLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                checkListError.postValue(e.message)
                checkListLoader.postValue(false)
            }

            override fun onComplete() {}
        }
        RepositoryCheckList().getCheckListDBByTipo(tipo, idCompany)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(10, TimeUnit.SECONDS)
                .subscribe(disposableObserverCheckList!!)
    }

    fun ValidateSurveyTime(context: Context, cliente: String?= ""){
        val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val staticStartValidationTime = formatter.parse("00:00")
        val staticEndValidationTime = formatter.parse("00:45")
        var serverStringTime = ""
        var serverTime: Date
        var call: Call<ApiResponseAnglo<String>>
        when(cliente){
            Companies.MC.valor ->{
                var apiMC = RestClient.buildMc()
                call = apiMC.getServerTime()
            }
            Companies.KRS.valor ->{
                var apiKRS = RestClient.buildKinross()
                call = apiKRS.getServerTime()
            }
            Companies.GF.valor ->{
                var apiGF = RestClient.buildGf()
                call = apiGF.getServerTime()
            }
            Companies.CDL.valor -> {
                var apiCDL = RestClient.buildCdl()
                call = apiCDL.getServerTime()
            }
            Companies.CAS.valor->{
                var apiCAS = RestClient.buildCaserones()
                call = apiCAS.getServerTime()
            }
            Companies.BR.valor -> {
                call = RestClient.buildBarrick().getServerTime()
            }
            else->{
                var apiCDL = RestClient.buildCdl()
                call = apiCDL.getServerTime()
            }
        }
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                if (response.isSuccessful) {
                    serverStringTime = response.body()!!.data
                    if (!serverStringTime.isNullOrEmpty()) {
                        serverTime = formatter.parse(serverStringTime)
                        if(serverTime.before(staticStartValidationTime) || serverTime.after(staticEndValidationTime))
                            ValidateWorkerSurvey(context, cliente)
                        else cdlResult.postValue(3)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                checkListError.postValue(t.message)
            }
        })

    }

    fun ValidateWorkerSurvey(context : Context, cliente: String?= ""){
        val call: Call<ApiResponseAnglo<Int>>
        when(cliente){
            Companies.MC.valor -> {
                val apiMc = RestClient.buildMc()
                call = apiMc.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            Companies.KRS.valor -> {
                val apiKrs = RestClient.buildKinross()
                call = apiKrs.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            Companies.GF.valor ->{
                val apiGF = RestClient.buildGf()
                call = apiGF.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            Companies.CDL.valor -> {
                val apiCDL = RestClient.buildCdl()
                call = apiCDL.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            Companies.CAS.valor ->{
                val apiCAS = RestClient.buildCaserones()
                call = apiCAS.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            Companies.QV.valor -> {
                call = RestClient.buildAnglo().getChecklistByDay(SharedUtils.getUsuarioId(context),SharedUtils.wCDate)
            }
            Companies.BR.valor -> {
                call = RestClient.buildBarrick().getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
            else->{
                val apiCDL = RestClient.buildCdl()
                call = apiCDL.getChecklistByDay(SharedUtils.getUsuarioId(context), SharedUtils.wCDate)
            }
        }
        call.enqueue(object : Callback<ApiResponseAnglo<Int>> {
            override fun onResponse(call: Call<ApiResponseAnglo<Int>>, response: Response<ApiResponseAnglo<Int>>) {
                if (response.isSuccessful) {
                    when(response.body()!!.data) //data = 0 -> worker puede dar encuesta // data = 1 worker ya tiene una encuesta // data = 2 -> indica worker esta bloqueado por 14 días
                    {
                        0->cdlResult.postValue(1) // no tiene encuesta llena para hoy
                        1->cdlResult.postValue(2) // tiene encuesta para hoy
                        2->cdlResult.postValue(4) // bloqueado por 14 días
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<Int>>, t: Throwable) {
                checkListError.postValue(t.message)
            }
        })
    }
}