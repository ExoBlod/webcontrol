package com.webcontrol.android.ui.common.adapters.holder

import android.view.View
import androidx.annotation.LayoutRes
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener
import com.webcontrol.android.ui.common.adapters.OnSelectedChangeListener
import com.webcontrol.android.ui.common.adapters.SekizbitSwitch
import kotlinx.android.synthetic.main.row_test_check_list.view.*

class SNViewHolder(val view: View, checklistType: String, private val buttonsDisabled: Boolean, val listener: CheckListAdapterListener) : BetterViewHolder<CheckListTest_Detalle>(view) {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_test_check_list
    }

    val switch: SekizbitSwitch = SekizbitSwitch(view.findViewById(R.id.sekizbit_switch))

    init {
        if (checklistType == "TFS" || checklistType == "ENC" || checklistType == "TMZ" || checklistType =="COV" ||checklistType == "EQV" || checklistType == "TPA"|| checklistType == "DDS" || checklistType == "VTP") {
            view.sekizbit_switch_btn_left.text = "NO"
            view.sekizbit_switch_btn_right.text = "SI"
        }
    }

    override fun bind(item: CheckListTest_Detalle) {
        val testDescripcion: String? = item.descripcion
        view.lblTestTitle.text = testDescripcion ?: "-"
        switch.setOnChangeListener(null)
        switch.cleanSelection()
        if (!buttonsDisabled) {
            applyClickEvents(item)
            if (!item.isUnset) {
                switch.setSelected(if (item.isChecked) 1 else 0)
            }
        } else {
            switch.setSelected(if (item.isChecked) 1 else 0)
            view.sekizbit_switch_btn_right.isClickable = false
            view.sekizbit_switch_btn_left.isClickable = false
        }
    }

    private fun applyClickEvents(item: CheckListTest_Detalle) {
        switch.setOnChangeListener(object : OnSelectedChangeListener {
            override fun onSelectedChange(sender: SekizbitSwitch?) {
                if (sender!!.checkedIndex == 0) {
                    item.isChecked = false
                    listener.onClickButtonNO(item)
                } else if (sender.checkedIndex == 1) {
                    listener.onClickButtonSI(item)
                    item.isChecked = true
                }
                item.isUnset = false
            }
        })
    }
}