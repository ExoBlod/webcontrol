package com.webcontrol.angloamerican.ui.bookcourses.ui.extravideo

import com.webcontrol.angloamerican.data.network.response.CourseContentData

sealed class ExtraVideoUIEvent {
    data class Success(val contentCourses: List<CourseContentData>) : ExtraVideoUIEvent()
    object Error : ExtraVideoUIEvent()
    object ShowLoading : ExtraVideoUIEvent()
    object HideLoading : ExtraVideoUIEvent()
}