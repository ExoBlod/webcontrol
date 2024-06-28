package com.webcontrol.android.data

import com.webcontrol.android.data.db.entity.CheckListTest_Detalle

interface OnCheckListener {

    fun onCheck(value: String, item: CheckListTest_Detalle)
}