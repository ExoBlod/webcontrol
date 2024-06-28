package com.webcontrol.android.ui.checklist

import android.text.Editable
import android.text.TextWatcher
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener

class CheckListEditTextListener(val listener: CheckListAdapterListener): TextWatcher {

    var item:CheckListTest_Detalle? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        item?.respuestaSeleccionada = s.toString()
        item?.let {
            listener.onTextChanged(it)
        }

    }
}