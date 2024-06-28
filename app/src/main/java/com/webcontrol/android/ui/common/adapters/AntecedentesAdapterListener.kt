package com.webcontrol.android.ui.common.adapters

import com.webcontrol.android.data.model.Antecedentes

interface AntecedentesAdapterListener {
    fun onClickButtonSI(item: Antecedentes)
    fun onClickButtonNO(item: Antecedentes)
    fun onTextChanged(item: Antecedentes)
}