package com.webcontrol.android.ui.newchecklist.views.consultinspections

import android.content.Context
import android.widget.EditText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.util.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConsultInspectionsViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
   private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){

    private val _listHistory = MutableSharedFlow<ConsultInspectionsUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()
    fun getHistoryByWorkerID(workerId: String, workerSearch: String, date: String, filter: Boolean) = launch {
        _listHistory.emit(ConsultInspectionsUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getWorkerHistoryById(workerId,workerSearch,date,filter)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(ConsultInspectionsUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(ConsultInspectionsUIEvent.Error)
            }
        }
        _listHistory.emit(ConsultInspectionsUIEvent.HideLoading)
    }

    fun getHistoryByPlate(plate: String, workerSearch: String, date: String, filter: Boolean) = launch {
        _listHistory.emit(ConsultInspectionsUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getWorkerHistoryByPlate(plate,workerSearch,date,filter)
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