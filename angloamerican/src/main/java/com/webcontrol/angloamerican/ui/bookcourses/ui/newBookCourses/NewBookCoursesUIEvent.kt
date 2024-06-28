package com.webcontrol.angloamerican.ui.bookcourses.ui.newBookCourses

import com.webcontrol.angloamerican.data.network.response.CourseData

sealed class NewBookCoursesUIEvent {
    data class Success(val listNewBookCourses: List<CourseData>) : NewBookCoursesUIEvent()
    object Error : NewBookCoursesUIEvent()
    object ShowLoading : NewBookCoursesUIEvent()
    object HideLoading : NewBookCoursesUIEvent()
}
