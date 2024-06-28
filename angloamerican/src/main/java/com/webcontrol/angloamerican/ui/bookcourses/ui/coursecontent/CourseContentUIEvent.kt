package com.webcontrol.angloamerican.ui.bookcourses.ui.coursecontent

import com.webcontrol.angloamerican.data.network.response.CourseContentData

sealed class CourseContentUIEvent {
    data class Success(val contentCourses: List<CourseContentData>) : CourseContentUIEvent()
    object Error : CourseContentUIEvent()
    object ShowLoading : CourseContentUIEvent()
    object HideLoading : CourseContentUIEvent()
}