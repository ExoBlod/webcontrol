package com.webcontrol.angloamerican.ui.bookcourses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webcontrol.angloamerican.data.network.response.CourseContentData
import com.webcontrol.angloamerican.data.network.response.ResultExam
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookCoursesViewModel @Inject
constructor(
): ViewModel() {
    private val _uiReserveCourseLiveData = MutableLiveData<ReserveCourseData>()
    private val uiReserveCourseLiveData: LiveData<ReserveCourseData>
        get() = _uiReserveCourseLiveData

    private val _contentCourses = MutableLiveData<List<CourseContentData>>()
    private val contentCourses: LiveData<List<CourseContentData>>
        get() = _contentCourses

    val _resultExamLiveData = MutableLiveData<ResultExam>()
    val resultExamLiveData: LiveData<ResultExam>
    get() = _resultExamLiveData

    val uiReserveCourse: ReserveCourseData get() = uiReserveCourseLiveData.value?:ReserveCourseData()
    val uiContentCourse: List<CourseContentData> get() = contentCourses.value?: listOf()
    val uiResultExam: ResultExam get () = resultExamLiveData.value?: ResultExam()

        fun setReserveCourse(reserveCourseData: ReserveCourseData) {
        _uiReserveCourseLiveData.value = reserveCourseData
    }

    fun setContentCourses(courseContentData: List<CourseContentData>) {
        _contentCourses.value = courseContentData
    }

    fun setResultExam (resultExam: ResultExam){
        _resultExamLiveData.value = resultExam
    }

    val currentContent: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun setCurrentContent(contentIndex: Int) {
        currentContent.value = contentIndex
    }
}