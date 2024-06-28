package com.webcontrol.android.angloamerican.ui.credential

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.angloamerican.data.repositories.CourseRepository
import com.webcontrol.angloamerican.data.CredentialCourse
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesCredentialViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    val coursesState: MutableLiveData<Resource<List<CredentialCourse>>> =
        MutableLiveData<Resource<List<CredentialCourse>>>()

    fun getCourses(workerId: String) {
        viewModelScope.launch {
            runCatching {
                repository.getCoursesAccreditation(workerId)
            }.onSuccess {
                coursesState.postValue(Resource.Success(it.data))
            }.onFailure {
                coursesState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}