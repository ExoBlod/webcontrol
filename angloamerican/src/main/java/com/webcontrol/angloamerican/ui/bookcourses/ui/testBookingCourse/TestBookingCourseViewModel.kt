package com.webcontrol.angloamerican.ui.bookcourses.ui.testBookingCourse

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.dto.SendQuestionsRequest
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.network.response.ResultExam
import com.webcontrol.angloamerican.domain.usecases.GetQuestionCourseUseCase
import com.webcontrol.angloamerican.domain.usecases.SendAnswersUseCase
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import com.webcontrol.angloamerican.utils.launch
import com.webcontrol.core.utils.LocalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TestBookingCourseViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    private val getQuestionCourseUseCase: GetQuestionCourseUseCase,
    private val sendAnswersUseCase: SendAnswersUseCase,
) : ViewModel() {

    private val _testBookingEvent = MutableSharedFlow<TestBookingCourseUIEvent>()
    val testBookingEvent get() = _testBookingEvent.asSharedFlow()


     fun getQuestionList(examId: Int) = launch {
        _testBookingEvent.emit(TestBookingCourseUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getQuestionCourseUseCase(examId)
            }.onSuccess { listQuestions ->
                _testBookingEvent.emit(TestBookingCourseUIEvent.Success(listQuestions))
            }.onFailure {
                _testBookingEvent.emit(TestBookingCourseUIEvent.Error)
            }
        }
        _testBookingEvent.emit(TestBookingCourseUIEvent.HideLoading)
    }

    fun sendQuestionList(
        idCharla: Int,
        idWorker: String,
        idEnterprise: String,
        listQuestions: List<QuestionData>
    ) = launch {
        _testBookingEvent.emit(TestBookingCourseUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                sendAnswersUseCase(idCharla, idWorker, idEnterprise, listQuestions)
            }.onSuccess { resultExam ->
                _testBookingEvent.emit(TestBookingCourseUIEvent.SuccessSendQuestionList(resultExam))
            }.onFailure {
                _testBookingEvent.emit(TestBookingCourseUIEvent.Error)
            }
        }
        _testBookingEvent.emit(TestBookingCourseUIEvent.HideLoading)
    }
}