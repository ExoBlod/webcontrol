package com.webcontrol.angloamerican.ui.bookcourses.ui.coursecontent

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetCoursesContentUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseContentViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    val getCoursesContentUseCase: GetCoursesContentUseCase
) : ViewModel() {
    
    private val _contentCourses = MutableSharedFlow<CourseContentUIEvent>()
    val currentContent: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val contentCourses get() = _contentCourses.asSharedFlow()
    fun getBookedCourses(idCourseProg: Int, idExamen: Int) = launch {
        _contentCourses.emit(CourseContentUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getCoursesContentUseCase(idCourseProg,idExamen)
            }.onSuccess { listNewChecklist ->
                _contentCourses.emit(CourseContentUIEvent.Success(listNewChecklist))
            }.onFailure {
                _contentCourses.emit(CourseContentUIEvent.Error)
            }
        }
        _contentCourses.emit(CourseContentUIEvent.HideLoading)
    }
}
