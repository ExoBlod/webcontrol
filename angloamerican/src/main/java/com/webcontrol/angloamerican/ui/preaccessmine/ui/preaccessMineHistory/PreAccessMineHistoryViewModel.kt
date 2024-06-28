package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineHistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetHistoryBookCoursesUseCase
import com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMinePassengers.PreAccessMinePassengersUIEvent
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.GetAllPreAccessMineUseCase
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.PostReservationPreaccessDetailMineUseCase
import com.webcontrol.angloamerican.ui.preaccessmine.usecases.PostReservationPreaccessMineUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreAccessMineHistoryViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getHistoryBookCoursesUseCase: GetAllPreAccessMineUseCase,
    private val postReservationPreaccessDetailMineUseCase: PostReservationPreaccessDetailMineUseCase,
    private val postReservationPreaccessMineUseCase: PostReservationPreaccessMineUseCase
) : ViewModel() {
    private val _listHistory = MutableSharedFlow<PreAccessMineHistoryUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()
    fun getHistory() = launch {
        _listHistory.emit(PreAccessMineHistoryUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getHistoryBookCoursesUseCase()
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(PreAccessMineHistoryUIEvent.Success(listNewChecklist))
            }.onFailure {
                val errorMessage = it.message.toString()
                _listHistory.emit(PreAccessMineHistoryUIEvent.Error(errorMessage))
            }
        }
        _listHistory.emit(PreAccessMineHistoryUIEvent.HideLoading)
    }
    fun postReservationPreaccessDetailMine() = launch{
        _listHistory.emit(PreAccessMineHistoryUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postReservationPreaccessDetailMineUseCase()
            }.onSuccess { validate ->
                _listHistory.emit(PreAccessMineHistoryUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(PreAccessMineHistoryUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMineHistoryUIEvent.HideLoading)

    }

    fun postReservationPreaccessMine() = launch{
        _listHistory.emit(PreAccessMineHistoryUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                postReservationPreaccessMineUseCase()
            }.onSuccess { validate ->
                _listHistory.emit(PreAccessMineHistoryUIEvent.SuccessValidation(validate))
            }.onFailure {
                _listHistory.emit(PreAccessMineHistoryUIEvent.Error(it.message.toString()))
            }
        }
        _listHistory.emit(PreAccessMineHistoryUIEvent.HideLoading)
    }
}