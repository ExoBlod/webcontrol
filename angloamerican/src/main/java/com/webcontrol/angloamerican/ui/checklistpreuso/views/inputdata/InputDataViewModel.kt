package com.webcontrol.angloamerican.ui.checklistpreuso.views.inputdata

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.InspectionUseCase
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.VehicleInformationUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputDataViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val vehicleInformationUseCase: VehicleInformationUseCase,
    private val inspectionUseCase: InspectionUseCase
) : ViewModel(){

    private val _eventUIInputData = MutableSharedFlow<InputDataUIEvent>()
    val eventUIInputData get() = _eventUIInputData.asSharedFlow()

    fun getVehicleInformation(plate: String) = launch {
        _eventUIInputData.emit(InputDataUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                vehicleInformationUseCase.invoke(plate)
            }.onSuccess { responsePlate ->
                _eventUIInputData.emit(InputDataUIEvent.Success(responsePlate))
            }.onFailure {
                _eventUIInputData.emit(InputDataUIEvent.Error)
            }
        }
        _eventUIInputData.emit(InputDataUIEvent.HideLoading)
    }

    fun validateInputData(request: InsertInspectionRequest)= launch {
        _eventUIInputData.emit(InputDataUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                inspectionUseCase.invoke(request)
                //newCheckListRepository.insertWorkerVehicleInformation(workerVehicleInformation)
            }.onSuccess { answerCheckingHead ->
                _eventUIInputData.emit(InputDataUIEvent.SuccessValidate(answerCheckingHead))
            }.onFailure {
                _eventUIInputData.emit(InputDataUIEvent.Error)
            }
        }
        _eventUIInputData.emit(InputDataUIEvent.HideLoading)
    }
}