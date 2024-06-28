package com.webcontrol.android.ui.newchecklist.views.inputdata

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.data.db.entity.InspeccionVehicular
import com.webcontrol.android.ui.newchecklist.data.VehicleInformation
import com.webcontrol.android.ui.newchecklist.data.WorkerSignature
import com.webcontrol.android.ui.newchecklist.data.WorkerVehicleInformation
import com.webcontrol.android.ui.newchecklist.views.signature.SignatureUIEvent
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.launch
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
   private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){

    private val _eventUIInputData = MutableSharedFlow<InputDataUIEvent>()
    val eventUIInputData get() = _eventUIInputData.asSharedFlow()

    fun getInfoCar(plate: String) = launch {
        _eventUIInputData.emit(InputDataUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getModelbyPlate(plate)
            }.onSuccess { responsePlate ->
                _eventUIInputData.emit(InputDataUIEvent.Success(responsePlate))
            }.onFailure {
                _eventUIInputData.emit(InputDataUIEvent.Error)
            }
        }
        _eventUIInputData.emit(InputDataUIEvent.HideLoading)
    }
    
    fun validateInputData(workerVehicleInformation: WorkerVehicleInformation)= launch {
        _eventUIInputData.emit(InputDataUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.insertWorkerVehicleInformation(workerVehicleInformation)
            }.onSuccess { answerCheckingHead ->
                _eventUIInputData.emit(InputDataUIEvent.SuccessValidate(answerCheckingHead))
            }.onFailure {
                _eventUIInputData.emit(InputDataUIEvent.Error)
            }
        }
        _eventUIInputData.emit(InputDataUIEvent.HideLoading)
    }
    
    
}