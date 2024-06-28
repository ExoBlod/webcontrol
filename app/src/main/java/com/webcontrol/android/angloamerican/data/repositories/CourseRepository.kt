package com.webcontrol.android.angloamerican.data.repositories

import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.angloamerican.data.CredentialCourse
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.angloamerican.data.CredentialCourseRequest

class CourseRepository(private val api: RestInterfaceAnglo) {
    suspend fun getCoursesAccreditation(workerId: String): ApiResponseAnglo<List<CredentialCourse>> {
        return api.getCoursesCredential(CredentialCourseRequest(workerId, 10))
    }
}