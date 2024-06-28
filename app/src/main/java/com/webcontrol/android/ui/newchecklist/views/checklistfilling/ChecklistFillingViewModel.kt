package com.webcontrol.android.ui.newchecklist.views.checklistfilling

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.util.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistFillingViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
   private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){
}