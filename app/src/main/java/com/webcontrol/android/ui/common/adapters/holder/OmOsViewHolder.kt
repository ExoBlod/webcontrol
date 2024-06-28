package com.webcontrol.android.ui.common.adapters.holder

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.common.adapters.TestAnswerAdapterListener
import com.webcontrol.android.ui.common.adapters.TestAnswersAdapter
import kotlinx.android.synthetic.main.row_question_om_os.view.*

class OmOsViewHolder(val view: View, val context: Context, val buttonsDisabled: Boolean, val checkListener: OnCheckListener?) : BetterViewHolder<CheckListTest_Detalle>(view), TestAnswerAdapterListener {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_question_om_os
    }

    override fun bind(item: CheckListTest_Detalle) {
        val testDescripcion: String = item.descripcion.toString()
        view.lblTestTitle!!.text = testDescripcion ?: "-"
        val layoutManager: RecyclerView.LayoutManager
        view.rcv_answers!!.visibility = View.VISIBLE
        val opcionesList: List<String> = item.getRespuestasConvert()!!
        //adaptador para preguntas de OM
        val testAnswersAdapter = TestAnswersAdapter(context, item, this, buttonsDisabled)
        layoutManager = LinearLayoutManager(context)
        view.rcv_answers!!.layoutManager = layoutManager
        view.rcv_answers!!.adapter = testAnswersAdapter
    }

    override fun onRowItemClick(value: String?, item: CheckListTest_Detalle) {
        if (checkListener != null) {
            item.respuestaSeleccionada = value.toString()
            if(item.tipo == "OMU"){
                view.rcv_answers.adapter = TestAnswersAdapter(context,item,this,buttonsDisabled)
                view.rcv_answers.adapter?.notifyDataSetChanged()
            }
            checkListener.onCheck(value!!, item)
            App.db!!.checkListDao().updateValorSeleccionadoCheckListDetalle(item.idDb, item.groupId, item.idTest, item.idTest_Detalle, item.respuestaSeleccionada)
        }
    }
}