package com.webcontrol.angloamerican.ui.approvemovements.ui.allmovements

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.approvemovements.approveruser.GetAllMovementsUseCase
import com.webcontrol.angloamerican.ui.bookcourses.ui.coursecontent.CourseContentUIEvent
import com.webcontrol.angloamerican.ui.checklistpreuso.views.listchecklist.ListChecklistUIEvent
import com.webcontrol.angloamerican.utils.launch
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMovementsViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    val getAllMovementsUseCase: GetAllMovementsUseCase
) : ViewModel() {

    init {
        getAllMovements()
    }

    private val _allMovements = MutableSharedFlow<AllMovementsUIEvent>()
    val allMovements get() = _allMovements.asSharedFlow()

    fun getAllMovements() = launch {
        _allMovements.emit(AllMovementsUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getAllMovementsUseCase(SharedUtils.getUsuarioId(context))
            }.onSuccess { listNewChecklist ->
                _allMovements.emit(AllMovementsUIEvent.HideLoading)
                _allMovements.emit(AllMovementsUIEvent.Success(listNewChecklist))
            }.onFailure {
                _allMovements.emit(AllMovementsUIEvent.HideLoading)
                _allMovements.emit(AllMovementsUIEvent.Error)
            }
        }
    }
}
