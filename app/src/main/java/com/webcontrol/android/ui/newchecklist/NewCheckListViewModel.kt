package com.webcontrol.android.ui.newchecklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.android.data.network.ApiResponseSearchBambas
import com.webcontrol.android.ui.newchecklist.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewCheckListViewModel @Inject
constructor(
) : ViewModel() {
    private val _uiStateLiveData = MutableLiveData<ApiResponseSearchBambas>()
    private val uiStateLiveData: LiveData<ApiResponseSearchBambas>
        get() = _uiStateLiveData

    private val _uiListGroupLiveData = MutableLiveData<List<NewCheckListGroup>>()
    private val uiListGroupLiveData: LiveData<List<NewCheckListGroup>>
        get() = _uiListGroupLiveData

    private val _uiVehicleLiveData = MutableLiveData<VehicleInformation>()
    private val uiVehicleLiveData: LiveData<VehicleInformation>
        get() = _uiVehicleLiveData

    private val _uiCheckingHeadLiveData = MutableLiveData<AnswerCheckingHead>()
    private val uiCheckingHeadLiveData: LiveData<AnswerCheckingHead>
        get() = _uiCheckingHeadLiveData

    private val _uiListQuestionLiveData = MutableLiveData<List<NewCheckListQuestion>>()
    private val uiListQuestionLiveData: LiveData<List<NewCheckListQuestion>>
        get() = _uiListQuestionLiveData

    private val _uiEnableBtnLiveData = MutableLiveData<Boolean>()
    private val uiEnableBtnLiveData: LiveData<Boolean>
        get() = _uiEnableBtnLiveData

    private val _uiHistoryListLiveData = MutableLiveData<List<NewCheckListHistory>>()
    private val uiHistoryListLiveData: LiveData<List<NewCheckListHistory>>
        get() = _uiHistoryListLiveData

    val uiState: ApiResponseSearchBambas get() = uiStateLiveData.value!!
    val uiVehicle: VehicleInformation get() = uiVehicleLiveData.value?:VehicleInformation()
    val uiListGroup: List<NewCheckListGroup> get() = uiListGroupLiveData.value?: listOf()
    val uiCheckingHead: AnswerCheckingHead get() = uiCheckingHeadLiveData.value?:AnswerCheckingHead()
    val uiListQuestion : List<NewCheckListQuestion> get() = uiListQuestionLiveData.value?: listOf()
    val uiEnableBtn : Boolean get() = uiEnableBtnLiveData.value?: true
    val uiHistoryList : List<NewCheckListHistory> get() = uiHistoryListLiveData.value ?: listOf()
    fun setResponseSearch(apiResponseSearchBambas: ApiResponseSearchBambas) {
        _uiStateLiveData.value = apiResponseSearchBambas
    }

    fun setListQuestions(list: List<NewCheckListGroup>) {
        _uiListGroupLiveData.value = list
    }

    fun setVehicleInformation(vehicleInformation: VehicleInformation) {
        _uiVehicleLiveData.value = vehicleInformation
    }

    fun setListQuestion(listQuestion: List<NewCheckListQuestion>) {
        _uiListQuestionLiveData.value = listQuestion
    }

    fun setEnableBtn(enabled: Boolean) {
        _uiEnableBtnLiveData.value = enabled
    }

    fun setHistoryList(list: List<NewCheckListHistory>) {
        _uiHistoryListLiveData.value = list
    }

    fun isCompleteGroup(): Boolean {
        uiListGroup.forEach {
            if (!it.isComplete()) return false
        }
        return true
    }

    fun setCheckingHead(answerCheckingHead: AnswerCheckingHead){
        _uiCheckingHeadLiveData.value = answerCheckingHead
    }
}