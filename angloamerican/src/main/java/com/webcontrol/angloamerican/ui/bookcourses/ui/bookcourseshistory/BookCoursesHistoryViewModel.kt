package com.webcontrol.angloamerican.ui.bookcourses.ui.bookcourseshistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetCredentialUseCase
import com.webcontrol.angloamerican.domain.usecases.GetHistoryBookCoursesUseCase
import com.webcontrol.angloamerican.utils.launch
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookCoursesHistoryViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getHistoryBookCoursesUseCase: GetHistoryBookCoursesUseCase
) : ViewModel() {
    private val _listHistory = MutableSharedFlow<HistoryBookCoursesUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()
    fun getHistory(workerId: String) = launch {
        _listHistory.emit(HistoryBookCoursesUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getHistoryBookCoursesUseCase(workerId)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(HistoryBookCoursesUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(HistoryBookCoursesUIEvent.Error)
            }
        }
        _listHistory.emit(HistoryBookCoursesUIEvent.HideLoading)
    }
}