package com.webcontrol.angloamerican.ui.bookcourses.ui.bookcourseshistory

import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData

sealed class HistoryBookCoursesUIEvent{
    data class Success(val listHistory: List<HistoryBookCourseData>) : HistoryBookCoursesUIEvent()
    data class SuccessSearchByCheckingHead(val listHistory: List<String>) : HistoryBookCoursesUIEvent()
    object Error : HistoryBookCoursesUIEvent()
    object ShowLoading : HistoryBookCoursesUIEvent()
    object HideLoading : HistoryBookCoursesUIEvent()
}