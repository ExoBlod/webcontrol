package com.webcontrol.android.ui.common.adapters

import com.webcontrol.android.data.db.entity.CheckListTest_Detalle

interface CheckListAdapterListener {
    fun onClickButtonSI(item: CheckListTest_Detalle)
    fun onClickButtonNO(item: CheckListTest_Detalle)
    fun onClickButtonNOApply(item: CheckListTest_Detalle)
    fun onTextChanged(item: CheckListTest_Detalle)
}