package com.webcontrol.angloamerican.ui.checklistpreuso.views.listchecklist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.AnswersUseCase
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.GetQuestionListUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListChecklistViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getQuestionListUseCase: GetQuestionListUseCase,
    private val answersUseCase: AnswersUseCase
) : ViewModel() {
    private val _listCheckList = MutableSharedFlow<ListChecklistUIEvent>()
    val listCheckList get() = _listCheckList.asSharedFlow()

    fun getListCheckList(typeVehicle: String) = launch {
        _listCheckList.emit(ListChecklistUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getQuestionListUseCase.invoke(typeVehicle)
            }.onSuccess { questionList->
                _listCheckList.emit(ListChecklistUIEvent.Success(questionList))
                _listCheckList.emit(ListChecklistUIEvent.HideLoading)
            }.onFailure {
                _listCheckList.emit(ListChecklistUIEvent.Error(it))
                _listCheckList.emit(ListChecklistUIEvent.HideLoading)
            }
        }
    }

    fun saveAnswers(listAnswer : List<QuestionListResponse>) = launch {
        _listCheckList.emit(ListChecklistUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                answersUseCase.invoke(listAnswer)
            }.onSuccess { listQuestion ->
                _listCheckList.emit(ListChecklistUIEvent.SuccessAnswerSaving(listQuestion))
                _listCheckList.emit(ListChecklistUIEvent.HideLoading)
            }.onFailure {
                _listCheckList.emit(ListChecklistUIEvent.Error(it))
                _listCheckList.emit(ListChecklistUIEvent.HideLoading)
            }
        }
    }
}