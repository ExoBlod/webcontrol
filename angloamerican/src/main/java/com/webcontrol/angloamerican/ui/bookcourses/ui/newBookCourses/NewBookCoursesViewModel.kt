package com.webcontrol.angloamerican.ui.bookcourses.ui.newBookCourses

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.domain.usecases.GetCoursesUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Result
import com.webcontrol.core.common.model.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class NewBookCoursesViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val getCoursesUseCase: GetCoursesUseCase
) : ViewModel() {
    private val _listCourses = MutableSharedFlow<NewBookCoursesUIEvent>()
    val listCourses get() = _listCourses.asSharedFlow()

    init {
        getCourses()
    }

    fun getCourses() = launch {
        viewModelScope.launch {
            _listCourses.emit(NewBookCoursesUIEvent.ShowLoading)
            runCatching {
                getCoursesUseCase()
            }.onSuccess { courses ->
                _listCourses.emit(NewBookCoursesUIEvent.Success(courses))
            }.onFailure {
                _listCourses.emit(NewBookCoursesUIEvent.Error)
            }
        }
        _listCourses.emit(NewBookCoursesUIEvent.HideLoading)
    }
}