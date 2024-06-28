package com.webcontrol.angloamerican.ui.bookcourses.ui.bookedCourses

import com.webcontrol.angloamerican.data.network.response.BookedCoursesData

sealed class BookedCoursesUIEvent {
    data class Success(val bookedCourses: List<BookedCoursesData>) : BookedCoursesUIEvent()
    object Error : BookedCoursesUIEvent()
    object ShowLoading : BookedCoursesUIEvent()
    object HideLoading : BookedCoursesUIEvent()
}