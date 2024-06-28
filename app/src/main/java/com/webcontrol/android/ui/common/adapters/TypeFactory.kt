package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.View
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.model.Alternativa
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.ui.common.adapters.holder.BetterViewHolder

interface TypeFactory {
    fun type(detail: CheckListTest_Detalle): Int

    fun type(detail: Antecedentes): Int

    fun holder(type: Int, view: View): BetterViewHolder<*>

    fun holder(type: Int, view: View, context: Context, checklistType: String, buttonsDisabled: Boolean, listener: CheckListAdapterListener, checkListener: OnCheckListener?): BetterViewHolder<*>

    fun holder(type: Int, view: View, listener: AntecedentesAdapterListener): BetterViewHolder<*>
}