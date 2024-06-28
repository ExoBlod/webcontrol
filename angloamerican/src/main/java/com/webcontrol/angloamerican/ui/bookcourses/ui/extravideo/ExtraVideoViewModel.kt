package com.webcontrol.angloamerican.ui.bookcourses.ui.extravideo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.domain.usecases.GetCoursesContentUseCase
import com.webcontrol.angloamerican.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtraVideoViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {
    val currentContent: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun setCurrentContent(contentIndex: Int) {
        currentContent.value = contentIndex
    }
}
