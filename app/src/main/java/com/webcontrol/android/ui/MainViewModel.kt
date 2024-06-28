package com.webcontrol.android.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.clientRepositories.BambasRepository
import com.webcontrol.android.data.clientRepositories.CollahuasiRepository
import com.webcontrol.android.data.clientRepositories.LaPoderosaRepository
import com.webcontrol.android.data.clientRepositories.PHCRepository
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.model.WorkerAnta
import com.webcontrol.android.data.model.WorkerBambas
import com.webcontrol.android.data.model.WorkerBarrick
import com.webcontrol.android.data.model.WorkerLaPoderosa
import com.webcontrol.android.data.network.ApiResponseSearchBambas
import com.webcontrol.android.data.network.LoginRequest
import com.webcontrol.android.data.network.TokenBambasRequest
import com.webcontrol.android.data.network.TokenPHCRequest
import com.webcontrol.android.data.network.TokenPoderosaRequest
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.util.RestClient
import com.webcontrol.pucobre.data.model.WorkerCredentialPucobre
import com.webcontrol.pucobre.data.model.WorkerPucobre
import com.webcontrol.pucobre.data.model.WorkerCredential
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(
    private val collahuasiRepository: CollahuasiRepository,
    private val bambasRepository: BambasRepository,
    private val poderosaRepository: LaPoderosaRepository,
    private val phcRepository: PHCRepository

) : ViewModel() {
    companion object {
        const val TAG = "MainViewModel"
    }

    private val isActive: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    private val workerBarrick: MutableLiveData<WorkerBarrick> = MutableLiveData<WorkerBarrick>()
    private val workerPucobre: MutableLiveData<WorkerPucobre> = MutableLiveData<WorkerPucobre>()
    private val workerPucobreCredential: MutableLiveData<WorkerCredential?> =
        MutableLiveData<WorkerCredential?>()
    private val CdlIsActive: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    private val McIsActive: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    private val telefonoCovid: MutableLiveData<String> = MutableLiveData<String>("")
    private val workerAnta: MutableLiveData<WorkerAnta?> = MutableLiveData(null)
    private val antaToken: MutableLiveData<String?> = MutableLiveData("")
    private val versionPlayStore: MutableLiveData<String> = MutableLiveData("")
    private val isBarrickActive: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isPucobreActive: MutableLiveData<Boolean> = MutableLiveData(false)
    val workerCollahuasi: MutableLiveData<WorkerAnglo?> = MutableLiveData()
    val tokenCollahuasi: MutableLiveData<String?> = MutableLiveData()
    val workerBambas: MutableLiveData<WorkerBambas?> = MutableLiveData()
    val tokenBambas: MutableLiveData<String?> = MutableLiveData()
    val tokenPHC: MutableLiveData<String?> = MutableLiveData()
    val tokenPoderosa: MutableLiveData<String?> = MutableLiveData()
    val workerPoderosa: MutableLiveData<WorkerLaPoderosa?> = MutableLiveData()
    val workerSearchBambas: MutableLiveData<ApiResponseSearchBambas?> = MutableLiveData()
    fun checkActive(): LiveData<Boolean> {
        return isActive
    }

    fun MccheckActive(): LiveData<Boolean> {
        return McIsActive
    }

    fun CdlcheckActive(): LiveData<Boolean> {
        return CdlIsActive
    }

    fun CdlTelefonoCov(): LiveData<String> {
        return telefonoCovid
    }

    fun checkWorkerBarrick(): LiveData<WorkerBarrick> = workerBarrick
    fun checkWorkerAnta(): LiveData<WorkerAnta?> = workerAnta
    fun checkWorkerPucobre(): LiveData<WorkerPucobre> = workerPucobre
    fun checkWorkerPucobreCredential(): LiveData<WorkerCredential?> = workerPucobreCredential
    fun checkAntaToken(): LiveData<String?> = antaToken
    fun checkVersionPlayStore(): LiveData<String> = versionPlayStore
    fun checkBarrickActive(): LiveData<Boolean> = isBarrickActive
    fun getTokenPHC() {
        viewModelScope.launch {
            runCatching {
                phcRepository.getToken(TokenPHCRequest(code = "Desa1.", deviceId = 1))
            }.onSuccess {
                if (it.token.isNotEmpty()) {
                    tokenPHC.postValue(it.token)
                }
            }.onFailure {
                it.printStackTrace()

            }
        }
    }

    fun getWorkerBarrick(workerId: String) {
        val api = RestClient.buildBarrick()
        viewModelScope.launch {
            runCatching {
                api.getWorker(workerId)
            }.onSuccess {
                if (it.data != null) {
                    workerBarrick.postValue(it.data!!)
                }
            }
        }
    }

    fun getWorkerGMP(workerId: String) {
        val api = RestClient.buildGmp()
        viewModelScope.launch {
            runCatching {
                api.getWorker(workerId)
            }.onSuccess {
                if (it.id > 0) {
                    isActive.postValue(true)
                }
            }
        }
    }

    fun getWorkerAnta(workerId: String) {
        val api = RestClient.buildAnta()
        GlobalScope.launch {
            runCatching {
                api.getWorker(workerId)
            }.onSuccess {
                if (it.isSuccess) {
                    it.data?.let { worker ->
                        workerAnta.postValue(worker)
                    }
                }
            }.onFailure {
                if (it is HttpException) {
                    if (it.code() == 401) {
                        val workerAnta = WorkerAnta()
                        workerAnta.rut = workerId
                        getToken(workerAnta)
                    }
                } else {
                    Log.e("TAG", "getWorkerAnta error: ${it.message}")
                }
            }
        }
    }

    private fun getToken(workerAnta: WorkerAnta) {
        val api = RestClient.buildAnta()
        GlobalScope.launch {
            runCatching {
                api.getToken(workerAnta)
            }.onSuccess {
                if (it.isSuccess) {
                    if (it.data.isNotEmpty()) {
                        antaToken.postValue(it.data)
                    }
                }
            }.onFailure {
                Log.e("TAG", "getToken error: ${it.message}")
            }
        }
    }

    fun getTokenBarrick(workerId: String) {
        val api = RestClient.buildBarrick()
        viewModelScope.launch {
            runCatching {
                api.getToken(LoginRequest(workerId))
            }.onSuccess { response ->
                if (response.isSuccess) {
                    val token = response.data ?: ""
                    if (token.isNotEmpty()) {
                        isBarrickActive.postValue(true)
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun getWorkerMc(workerId: String) {
        val api = RestClient.buildMc()
        GlobalScope.launch {
            runCatching {
                api.getWorker(workerId)
            }.onSuccess {
                if (it.data == 1) {
                    McIsActive.postValue(true)
                } else
                    McIsActive.postValue(false)
            }
        }
    }

    fun getWorkerCDL(workerId: String) {
        val api = RestClient.buildCdl()
        GlobalScope.launch {
            runCatching {
                api.getWorker(workerId)
            }.onSuccess {
                if (it.data == 1) {
                    CdlIsActive.postValue(true)
                } else
                    CdlIsActive.postValue(false)
            }
        }
    }

    fun getWorkerCollahuasi(workerId: String) {
        viewModelScope.launch {
            runCatching {
                collahuasiRepository.getWorkerInfo(
                    WorkerRequest(
                        workerId = workerId
                    )
                )
            }.onSuccess {
                if (it.isSuccess) {
                    workerCollahuasi.postValue(it.data)
                }
            }.onFailure {
                it.printStackTrace()
                if (it is HttpException) {
                    if (it.code() == 401) {
                        getTokenCollahuasi(WorkerRequest(workerId = workerId))
                    }
                }
            }
        }
    }

    private fun getTokenCollahuasi(workerRequest: WorkerRequest) {
        viewModelScope.launch {
            runCatching {
                collahuasiRepository.login(workerRequest)
            }.onSuccess {
                if (it.isSuccess) {
                    tokenCollahuasi.postValue(it.data)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun getWorkerBambas(workerId: String) {
        viewModelScope.launch {
            runCatching {
                bambasRepository.getCredentials(workerId)
            }.onSuccess {
                if (it.isSuccess) {
                    workerBambas.postValue(it.data)
                }
            }.onFailure {
                it.printStackTrace()
                if (it is HttpException) {
                    if (it.code() == 401) {
                        getTokenBambas(workerId)
                    }
                }
            }
        }
    }

    fun isDriverBambas(workerId: String) {
        try {
            var workerData: WorkerBambas? = null
            viewModelScope.launch {
                val response = bambasRepository.getCredentials(workerId)
                if (response.isSuccess) {
                    workerData = response.data
                    workerBambas.postValue(workerData)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException && e.code() == 401) {
                getTokenBambas(workerId)
            }
        }
    }

    fun searchBambas(workerId: String) {
        viewModelScope.launch {
            runCatching {
                bambasRepository.getSearchWorker(workerId)
            }.onSuccess {
                if (it.isNotEmpty()) {
                    isDriverBambas(workerId)
                    val workerB = it[0]
                    workerSearchBambas.postValue(workerB)
                }
            }.onFailure {
                it.printStackTrace()

            }
        }
    }

    fun searchPoderosa(workerId: String) {
        viewModelScope.launch {
            runCatching {
                poderosaRepository.getCredentials(workerId)
            }.onSuccess {
                if (it.isSuccess) {
                    workerPoderosa.postValue(it.data)
                }
            }.onFailure {
                it.printStackTrace()
                if (it is HttpException) {
                    if (it.code() == 401) {
                        getTokenPoderosa(workerId)
                    }
                }
            }
        }
    }

    fun getTokenBambas(workerId: String) {
        viewModelScope.launch {
            runCatching {
                bambasRepository.getToken(TokenBambasRequest(workerId = workerId))
            }.onSuccess {
                if (it.token.isNotEmpty()) {
                    tokenBambas.postValue(it.token)
                }
            }.onFailure {
                it.printStackTrace()

            }
        }
    }

    fun getTokenPoderosa(workerId: String) {
        viewModelScope.launch {
            runCatching {
                poderosaRepository.getToken(TokenPoderosaRequest(workerId = workerId))
            }.onSuccess {
                if (it.token.isNotEmpty()) {
                    tokenPoderosa.postValue(it.token)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun getWorkerPucobre(workerId: String) {
        val api = RestClient.buildPucobre()
        viewModelScope.launch {
            runCatching {
                api.getCredential(workerId)
            }.onSuccess {
                if(it.success){
                    workerPucobreCredential.postValue(it.data)
                }
            }
        }
    }
}