package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.View
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.ui.checklist.CheckListEditTextListener
import com.webcontrol.android.ui.common.adapters.holder.*

class TypeFactoryImpl : TypeFactory {
    override fun type(detail: CheckListTest_Detalle): Int {
        if (detail.tipo != null) {
            when (detail.tipo) {
                "SN" -> return SNViewHolder.LAYOUT
                "OM" -> return OmOsViewHolder.LAYOUT
                "OS" -> return OmOsViewHolder.LAYOUT
                "TXT" -> return TxtViewHolder.LAYOUT
                "DATE" -> return DateViewHolder.LAYOUT
                "OMU" -> return OmOsViewHolder.LAYOUT
                "STAR" -> return StarViewHolder.LAYOUT
                //"TRAV" -> return TrpeTravViewHolder.LAYOUT
                //"TRPE" -> return TrpeTravViewHolder.LAYOUT
            }
        }
        if(detail.typeCheckList != null && detail.typeCheckList == "TDV")
            return TDVViewHolder.LAYOUT
        return SNViewHolder.LAYOUT
    }

    override fun type(detail: Antecedentes): Int {
        if (detail.tipo != null) {
            when (detail.tipo) {
                "SWITCH" -> return SwitchViewHolder.LAYOUT
                "TXT" -> return TxtViewAntecedHolder.LAYOUT
            }
        }
        return SwitchViewHolder.LAYOUT
    }

    override fun holder(type: Int, view: View): BetterViewHolder<*> {
        return when (type) {
            else -> throw RuntimeException("Ilegal view type")
        }
    }



    override fun holder(type: Int, view: View, context: Context, checklistType: String, buttonsDisabled: Boolean, listener: CheckListAdapterListener, checkListener: OnCheckListener?): BetterViewHolder<*> {
        return when (type) {
            TDVViewHolder.LAYOUT -> TDVViewHolder(view, checklistType, buttonsDisabled, listener)
            SNViewHolder.LAYOUT -> SNViewHolder(view, checklistType, buttonsDisabled, listener)
            OmOsViewHolder.LAYOUT -> OmOsViewHolder(view, context, buttonsDisabled, checkListener)
            TxtViewHolder.LAYOUT -> TxtViewHolder(view,CheckListEditTextListener(listener),buttonsDisabled)//textoAdapterListener
            StarViewHolder.LAYOUT -> StarViewHolder(view, checklistType, buttonsDisabled, listener)
            //TrpeTravViewHolder.LAYOUT -> TrpeTravViewHolder(view)
            DateViewHolder.LAYOUT -> DateViewHolder(view,context, CheckListEditTextListener(listener),buttonsDisabled)
            else -> throw RuntimeException("Ilegal view type")
        }
    }

    override fun holder(type: Int, view: View, listener: AntecedentesAdapterListener): BetterViewHolder<*> {
        return when (type) {
            SwitchViewHolder.LAYOUT -> SwitchViewHolder(view, listener)
            TxtViewAntecedHolder.LAYOUT -> TxtViewAntecedHolder(view, listener)
            else -> throw RuntimeException("Ilegal view type")
        }
    }
}