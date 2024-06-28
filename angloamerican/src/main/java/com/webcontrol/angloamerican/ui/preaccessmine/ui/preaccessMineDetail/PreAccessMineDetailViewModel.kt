package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetHistoryBookCoursesUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreAccessMineDetailViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getHistoryBookCoursesUseCase: GetHistoryBookCoursesUseCase
) : ViewModel() {
    private val _listHistory = MutableSharedFlow<PreAccessMineDetailUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()
    fun getHistory(workerId: String) = launch {
        _listHistory.emit(PreAccessMineDetailUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getHistoryBookCoursesUseCase(workerId)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(PreAccessMineDetailUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(PreAccessMineDetailUIEvent.Error)
            }
        }
        _listHistory.emit(PreAccessMineDetailUIEvent.HideLoading)
    }
}