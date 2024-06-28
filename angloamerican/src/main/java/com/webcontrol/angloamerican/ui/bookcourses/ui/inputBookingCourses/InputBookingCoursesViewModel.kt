package com.webcontrol.angloamerican.ui.bookcourses.ui.inputBookingCourses

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.SendInputCourseUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputBookingCoursesViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val sendInputCourseUseCase: SendInputCourseUseCase
) : ViewModel() {
    private val _successSending = MutableSharedFlow<InputBookingCoursesUIEvent>()
    val successSending get() = _successSending.asSharedFlow()
    fun sendCourse(workerId: String, idCourse: Int, startDate: String) = launch {
        viewModelScope.launch {
            runCatching {
                sendInputCourseUseCase(workerId, idCourse, startDate)
            }.onSuccess { success ->
                _successSending.emit(InputBookingCoursesUIEvent.Success(success))
            }.onFailure {
                _successSending.emit(InputBookingCoursesUIEvent.Error)
            }
        }
    }
}