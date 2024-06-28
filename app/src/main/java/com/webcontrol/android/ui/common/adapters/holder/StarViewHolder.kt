package com.webcontrol.android.ui.common.adapters.holder

import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.annotation.LayoutRes
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener
import com.webcontrol.android.ui.common.adapters.SekizbitSwitch
import kotlinx.android.synthetic.main.row_question_star.view.*
import kotlinx.android.synthetic.main.row_test_check_list.view.lblTestTitle


class StarViewHolder(val view: View, checklistType: String, private val buttonsDisabled: Boolean, val listener: CheckListAdapterListener) : BetterViewHolder<CheckListTest_Detalle>(view) {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_question_star
    }

    val switch: SekizbitSwitch = SekizbitSwitch(view.findViewById(R.id.sekizbit_switch))

    override fun bind(item: CheckListTest_Detalle) {
        switch.setOnChangeListener(null)
        switch.cleanSelection()
        val testDescripcion: String? = item.descripcion
        view.lblTestTitle.text = testDescripcion ?: "-"
        val respuestas:MutableList<String> = item.respuestas!!.split(",").toMutableList()
        val respuestasIndices:MutableList<String> =
                item.respuestaSeleccionada!!.split(",").map { it.trim() }.toMutableList()
        view.rating.numStars = respuestas.size
        view.rating.stepSize = 1.0f
        respuestas[0] = respuestas[0].substring(1)
        respuestas[respuestas.lastIndex] = respuestas.last().substring(0,respuestas.last().length-1)
        respuestasIndices[0] = respuestasIndices[0].substring(1)
        respuestasIndices[respuestasIndices.lastIndex] = respuestasIndices.last().substring(0,respuestasIndices.last().length-1)

        if (!buttonsDisabled) {
            applyClickEvents(item)
        } else {
            view.rating.setIsIndicator(true)
            val itemBuscar = item.groupId.toString()
            val indiceRes = respuestasIndices.indexOfFirst { it == itemBuscar }
            view.rating.rating = (indiceRes+1).toFloat()
            view.mRatingScale.text = respuestas[indiceRes]
        }
    }

    private fun applyClickEvents(item: CheckListTest_Detalle) {
        val respuestas:MutableList<String> = item.respuestas!!.split(",").toMutableList()
        val respuestasIndices:MutableList<String> =
                item.respuestaSeleccionada!!.split(",").map { it.trim() }.toMutableList()
        respuestas[0] = respuestas[0].substring(1)
        respuestas[respuestas.lastIndex] = respuestas.last().substring(0,respuestas.last().length-1)
        respuestasIndices[0] = respuestasIndices[0].substring(1)
        respuestasIndices[respuestasIndices.lastIndex] = respuestasIndices.last().substring(0,respuestasIndices.last().length-1)
        view.rating.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, v, b ->
            view.mRatingScale.text = java.lang.String.valueOf(v)
            when (ratingBar.rating.toInt()) {
                1 -> view.mRatingScale.text = respuestas[0]
                2 -> view.mRatingScale.text = respuestas[1]
                3 -> view.mRatingScale.text = respuestas[2]
                4 -> view.mRatingScale.text = respuestas[3]
                5 -> view.mRatingScale.text = respuestas[4]
                else -> view.mRatingScale.text = ""
            }
            item.estado = ratingBar.rating.toInt()
            item.groupId = respuestasIndices[ratingBar.rating.toInt()-1].trim().toInt()
            listener.onClickButtonSI(item)
        }
    }

    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItemViewType(position: Int): Int {
        return position
    }
}

