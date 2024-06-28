package com.webcontrol.angloamerican.ui.checklistpreuso.views.consultinspections

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.*
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.HistoryRepository
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ConsultInspectionsViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val historyRepository: HistoryRepository
) : ViewModel(){

    private val _listHistory = MutableSharedFlow<ConsultInspectionsUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()


    fun getHistoryByWorkerID(request: HistoryByWorkerIdRequest) = launch {
        _listHistory.emit(ConsultInspectionsUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                historyRepository.getHistoryById(request)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(ConsultInspectionsUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(ConsultInspectionsUIEvent.Error)
            }
        }
        _listHistory.emit(ConsultInspectionsUIEvent.HideLoading)
    }

    fun getHistoryByPlate(request: HistoryByPlateRequest) = launch {
        _listHistory.emit(ConsultInspectionsUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                historyRepository.getHistoryByPlate(request)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(ConsultInspectionsUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(ConsultInspectionsUIEvent.Error)
            }
        }
        _listHistory.emit(ConsultInspectionsUIEvent.HideLoading)
    }


    fun getYear(): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return year
    }

    fun getMonth(): Int {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        return month
    }

    fun getDayOfMonth(): Int {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return dayOfMonth
    }
}