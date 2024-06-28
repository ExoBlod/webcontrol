package com.webcontrol.angloamerican.ui.checklistpreuso.views.sworndeclaration

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SwornDeclarationViewModel @Inject
constructor(
    @ApplicationContext val context: Context
) : ViewModel(){

}