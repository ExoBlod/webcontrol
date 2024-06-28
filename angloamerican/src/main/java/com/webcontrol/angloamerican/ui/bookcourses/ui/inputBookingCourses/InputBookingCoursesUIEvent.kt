package com.webcontrol.angloamerican.ui.bookcourses.ui.inputBookingCourses

sealed class InputBookingCoursesUIEvent {
    data class Success(val success: Boolean) : InputBookingCoursesUIEvent()
    object Error : InputBookingCoursesUIEvent()
    object ShowLoading : InputBookingCoursesUIEvent()
    object HideLoading : InputBookingCoursesUIEvent()
}
