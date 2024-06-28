package com.webcontrol.android.ui.common.adapters

import com.webcontrol.android.data.db.entity.CheckListTest_Detalle

interface TestAnswerAdapterListener {

    fun onRowItemClick(value: String?, item: CheckListTest_Detalle)
}