package com.webcontrol.android.ui.capacitaciones

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.data.model.Capacitaciones
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.network.BarrickResponse
import com.webcontrol.android.data.network.CourseRequest
import com.webcontrol.android.util.RestClient
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CapacitacionesViewModel @Inject constructor() : ViewModel() {

    private val api = RestClient.buildBarrick()
    private val apiEA = RestClient.buildEA()
    val cursoListState = MutableLiveData<Resource<BarrickResponse<List<Capacitaciones>>>>()
    val divisionState = MutableLiveData<Resource<BarrickResponse<List<Division>>>>()

    fun getDivisions() {
        viewModelScope.launch {
            runCatching {
                divisionState.postValue(Resource.Loading(true))
                api.getDivisionList()
            }.onSuccess {
                divisionState.postValue(Resource.Success(it))
            }.onFailure {
                divisionState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun getDivisionsEA() {
        viewModelScope.launch {
            runCatching {
                divisionState.postValue(Resource.Loading(true))
                apiEA.getDivisionList()
            }.onSuccess {
                divisionState.postValue(Resource.Success(it))
            }.onFailure {
                divisionState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun getCoursesList(request:CourseRequest) {
        viewModelScope.launch {
            runCatching {
                cursoListState.postValue(Resource.Loading(true))
                api.getCourses(request)
            }.onSuccess {
                cursoListState.postValue(Resource.Success(it))
            }.onFailure {
                cursoListState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun getCoursesListEA(rut : String, division: String) {
        viewModelScope.launch {
            runCatching {
                cursoListState.postValue(Resource.Loading(true))
                apiEA.getCourses(rut, division)
            }.onSuccess {
                cursoListState.postValue(Resource.Success(it))
            }.onFailure {
                cursoListState.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

}