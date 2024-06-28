package com.webcontrol.angloamerican.ui.bookcourses.ui.bookedCourses

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetBookedCoursesUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedCoursesViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val getBookedCoursesUseCase: GetBookedCoursesUseCase
) : ViewModel() {

    private val _bookedCourses = MutableSharedFlow<BookedCoursesUIEvent>()
    val bookedCourses get() = _bookedCourses.asSharedFlow()

    fun getBookedCourses(workerId: String) = launch {
        _bookedCourses.emit(BookedCoursesUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getBookedCoursesUseCase(workerId)
            }.onSuccess { listNewChecklist ->
                _bookedCourses.emit(BookedCoursesUIEvent.Success(listNewChecklist))
            }.onFailure {
                _bookedCourses.emit(BookedCoursesUIEvent.Error)
            }
        }
        _bookedCourses.emit(BookedCoursesUIEvent.HideLoading)
    }
}