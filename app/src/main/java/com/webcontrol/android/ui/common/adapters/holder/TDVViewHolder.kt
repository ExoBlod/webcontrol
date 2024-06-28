package com.webcontrol.android.ui.common.adapters.holder

import android.view.View
import androidx.annotation.LayoutRes
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener
import com.webcontrol.android.ui.common.adapters.OnSelectedChangeListener
import com.webcontrol.android.ui.common.adapters.SekizbitSwitch
import kotlinx.android.synthetic.main.row_test_check_list_v2.view.*

class TDVViewHolder(val view: View, checklistType: String, private val buttonsDisabled: Boolean, val listener: CheckListAdapterListener) : BetterViewHolder<CheckListTest_Detalle>(view) {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_test_check_list_v2
    }

    val switch: SekizbitSwitch = SekizbitSwitch(view.findViewById(R.id.sekizbit_switch_v2))

    init {
        if (checklistType == "TFS" || checklistType == "ENC" || checklistType == "TMZ" || checklistType =="COV" ||checklistType == "EQV" || checklistType == "TPA"|| checklistType == "DDS" || checklistType == "VTP") {
            view.sekizbit_switch_btn_left2.text = "NO"
            view.sekizbit_switch_btn_right2.text = "SI"
        }
        if(checklistType == "TDV")
            view.sekizbit_switch_btn_no_apply.text = "NO APLICA"
    }

    override fun bind(item: CheckListTest_Detalle) {
        val testTitle: String ?= item.title
        val testDescripcion: String ?= item.descripcion
        view.lblTestTitle.text = testTitle ?: "-"
        view.lblTestDescription.text = testDescripcion ?: "-"
        switch.setOnChangeListener(null)
        switch.cleanSelection()
        if (item.tipo == "SN") {
            view.sekizbit_switch_btn_no_apply.visibility = View.GONE
        } else {
            view.sekizbit_switch_btn_no_apply.visibility = View.VISIBLE
        }
        if (!buttonsDisabled) {

            applyClickEvents(item)
            if (!item.isUnset) {
                if(!item.valorChecked.isNullOrEmpty()){
                    when(item.valorChecked){
                        "NO" -> switch.setSelected(0)
                        "NA" -> switch.setSelected(1)
                        "SI" -> switch.setSelected(2)
                        else -> switch.setSelected(0)
                    }

                } else
                    switch.setSelected(if (item.isChecked) 1 else 0)
            }
        } else {
            if(!item.valorChecked.isNullOrEmpty()){
                when(item.valorChecked){
                    "NO" -> switch.setSelected(0)
                    "NA" -> switch.setSelected(1)
                    "SI" -> switch.setSelected(2)
                    else -> switch.setSelected(0)
                }
            }
            else
                switch.setSelected(if (item.isChecked) 1 else 0)
            view.sekizbit_switch_btn_right2.isClickable = false
            view.sekizbit_switch_btn_left2.isClickable = false
            view.sekizbit_switch_btn_no_apply.isClickable = false
        }
        if(item.typeCheckList == "TDV" && (item.title != item.descripcion) )
        {
            view.lblTestDescription.visibility = View.VISIBLE
        }
        else
            view.lblTestDescription.visibility = View.GONE
    }

    private fun applyClickEvents(item: CheckListTest_Detalle) {
        switch.setOnChangeListener(object : OnSelectedChangeListener {

            override fun onSelectedChange(sender: SekizbitSwitch?) {
                if (sender!!.checkedIndex == 0) {
                    item.isChecked = false
                    item.valorChecked = "NO"
                    listener.onClickButtonNO(item)
                } else if (sender.checkedIndex == 2) {
                    listener.onClickButtonSI(item)
                    item.isChecked = true
                    item.valorChecked = "SI"
                }else if (sender.checkedIndex == 1) {
                    listener.onClickButtonNOApply(item)
                    item.isChecked = false
                    item.valorChecked = "NA"
                }
                item.isUnset = false
            }
        })
    }
}
