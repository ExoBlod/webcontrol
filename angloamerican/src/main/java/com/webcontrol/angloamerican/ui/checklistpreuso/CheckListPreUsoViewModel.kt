package com.webcontrol.angloamerican.ui.checklistpreuso

import android.provider.SyncStateContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.internal.Constants
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ContentPreUso
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListQuestion
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.ApiResponseSearchAnglo
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.*
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.CredentialSearchUserRepository
import com.webcontrol.angloamerican.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckListPreUsoViewModel @Inject
constructor(
    private val credentialSearchUserRepository: CredentialSearchUserRepository,
): ViewModel() {

    private val STATUS_E1 = "E1"

    private val _uiStateLiveData = MutableLiveData<WorkerResponse>()
    private val uiStateLiveData: LiveData<WorkerResponse>
        get() = _uiStateLiveData

    private val _uiStateLiveApiData = MutableLiveData<ApiResponseSearchAnglo>()
    private val uiStateLiveApiData: LiveData<ApiResponseSearchAnglo>
        get() = _uiStateLiveApiData

    private val _isSearchingLiveData = MutableLiveData<Boolean>()
    private val isSearchingLiveData: LiveData<Boolean>
        get() = _isSearchingLiveData

    private val _uiCheckListPreUso = MutableLiveData<ContentPreUso>()
    private val uiCheckListPreUsoLiveData: LiveData<ContentPreUso>
        get() = _uiCheckListPreUso

    private val _uiCheckingHeadLiveData = MutableLiveData<Int>()
    private val uiCheckingHeadLiveData: LiveData<Int>
        get() = _uiCheckingHeadLiveData


    private val _uiListGroupLiveData = MutableLiveData<List<QuestionListResponse>>()
    val uiListGroupLiveData: LiveData<List<QuestionListResponse>>
        get() = _uiListGroupLiveData

    private val _uiListGroupPreUsoLiveData = MutableLiveData<List<QuestionListByIdResponse>>()
    val uiListGroupPreUsoLiveData: LiveData<List<QuestionListByIdResponse>>
        get() = _uiListGroupPreUsoLiveData

    private val _uiListQuestionLiveData = MutableLiveData<List<NewCheckListQuestion>>()
    private val uiListQuestionLiveData: LiveData<List<NewCheckListQuestion>>
        get() = _uiListQuestionLiveData

    private val _uiVehicleLiveData = MutableLiveData<VehicleInformationResponse>()
    private val uiVehicleLiveData: LiveData<VehicleInformationResponse>
        get() = _uiVehicleLiveData

    private var _uiDisableBtnLiveData = MutableLiveData<Boolean>()
    private val uiDisableBtnLiveData: LiveData<Boolean>
        get() = _uiDisableBtnLiveData

    private var _uiClearHistoryLiveData = MutableLiveData<Boolean>()
    private val uiClearHistoryLiveData: LiveData<Boolean>
        get() = _uiClearHistoryLiveData

    private val _uiHistoryListLiveData = MutableLiveData<List<HistoryResponse>>()
    private val uiHistoryListLiveData: LiveData<List<HistoryResponse>>
        get() = _uiHistoryListLiveData

    private val _uiHistoryListByPlateLiveData = MutableLiveData<List<HistoryResponse>>()
    private val uiHistoryListByPlateLiveData: LiveData<List<HistoryResponse>>
        get() = _uiHistoryListByPlateLiveData

    private val _uiHistoryListByWorkerLiveData = MutableLiveData<List<HistoryResponse>>()
    private val uiHistoryListByWorkerLiveData: LiveData<List<HistoryResponse>>
        get() = _uiHistoryListByWorkerLiveData

    private val _uiCheckListStatusLiveData = MutableLiveData<Boolean>()
    private val uiCheckListStatusLiveData: LiveData<Boolean>
        get() = _uiCheckListStatusLiveData

    val uiCheckListPreUso: ContentPreUso get() = uiCheckListPreUsoLiveData.value?: ContentPreUso("",false, "")
    val uiStateWorker: WorkerResponse get() = uiStateLiveData.value!!
    val uiState: ApiResponseSearchAnglo get() = uiStateLiveApiData.value!!
    val isSearching: Boolean get() = isSearchingLiveData.value ?: false
    val uiVehicle: VehicleInformationResponse get() = uiVehicleLiveData.value ?: VehicleInformationResponse()
    val uiListGroup: List<QuestionListResponse> get() = uiListGroupLiveData.value?: listOf()
    val uiCheckingHead: Int get() = uiCheckingHeadLiveData.value ?: 0
    val uiHistoryList : List<HistoryResponse> get() = uiHistoryListLiveData.value ?: listOf()
    val uiClearHistory : Boolean get() = _uiClearHistoryLiveData.value ?: false
    val uiDisabledBtn : Boolean get() = _uiDisableBtnLiveData.value ?: false
    val uiHistoryListByPlate : List<HistoryResponse> get() = uiHistoryListByPlateLiveData.value ?: listOf()
    val uiHistoryListByWorker : List<HistoryResponse> get() = uiHistoryListByWorkerLiveData.value ?: listOf()

    val workerSearchAnglo: MutableLiveData<WorkerResponse?> = MutableLiveData()
    val uiCheckListStatus : Boolean get() = _uiCheckListStatusLiveData.value ?: false

    fun setResponseSearch(workerResponse: WorkerResponse) {
        _uiStateLiveData.value = workerResponse
    }

    fun setCheckListPreUso(contentPreUso: ContentPreUso) {
        _uiCheckListPreUso.value = contentPreUso
    }
    fun setIsSearching(isSearching: Boolean) {
        _isSearchingLiveData.value = isSearching
    }
    fun isCompleteGroup(): Boolean {
        uiListGroup.forEach {
            if (!it.isComplete()) return false
        }
        return true
    }

    fun setListQuestions(list: List<QuestionListResponse>) {
        _uiListGroupLiveData.value = list
    }

    fun cleanQuestionHistory(){
        _uiListGroupLiveData.value = emptyList()
    }

    fun setListQuestionsPreUso(list: List<QuestionListByIdResponse>) {
        _uiListGroupPreUsoLiveData.value = list
    }

    fun setListQuestion(listQuestion: List<NewCheckListQuestion>) {
        _uiListQuestionLiveData.value = listQuestion
    }

    fun setVehicleInformation(vehicleInformation: VehicleInformationResponse) {
        _uiVehicleLiveData.value = vehicleInformation
    }

    fun setCheckingHead(checkingHead: Int){
        _uiCheckingHeadLiveData.value = checkingHead
    }

    fun setHistoryList(list: List<HistoryResponse>) {
        _uiHistoryListLiveData.value = list
    }

    fun setHistoryListByPlate(list: List<HistoryResponse>) {
        _uiHistoryListByPlateLiveData.value = list
    }

    fun setHistoryListByDni(list: List<HistoryResponse>) {
        _uiHistoryListByWorkerLiveData.value = list
    }

    fun setIsConsulting(isActive: Boolean){
        _uiDisableBtnLiveData.value = isActive
    }

    fun setCleanHistory(isActive: Boolean){
        _uiClearHistoryLiveData.value = isActive
    }

    fun setCheckListStatus(isPending : String){
        _uiCheckListStatusLiveData.value = isPending == STATUS_E1
    }

    fun searchAnglo(workerId: WorkerRequest) {
        viewModelScope.launch {
            runCatching {
                credentialSearchUserRepository.getCredentialSearchUser(workerId)
            }.onSuccess {
                if (it.isNotEmpty()) {
                    val workerB = it[0]
                    workerSearchAnglo.postValue(workerB)
                }
            }.onFailure {
                it.printStackTrace()

            }
        }
    }
}