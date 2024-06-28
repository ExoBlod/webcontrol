package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import kotlinx.android.synthetic.main.row_test_answer.view.*
import java.util.*
import kotlin.collections.ArrayList

class TestAnswersAdapter(private val mContext: Context, private val checkListTestDetalle: CheckListTest_Detalle, private val listener: TestAnswerAdapterListener, private val buttonsDisabled: Boolean) : RecyclerView.Adapter<TestAnswersAdapter.MyViewHolder>() {
    private val parentPosition = 0
    private val testOptions: List<String>?= checkListTestDetalle.getRespuestasConvert()
    private var respMarcadas: MutableList<String>?

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var rbAnswer: Chip = itemView.rb_answer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_test_answer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            //obtiene respuestas marcadas anteriormente
            if (checkListTestDetalle.getRespuestaSeleccionadaConvert()!= null)
                respMarcadas = checkListTestDetalle.getRespuestaSeleccionadaConvert()!!.toMutableList()
            if (testOptions != null) {
                holder.rbAnswer.text = testOptions[position]
                if (!buttonsDisabled) {
                    holder.rbAnswer.isSelected = false
                    if (respMarcadas!!.contains(holder.rbAnswer.text.toString())) {
                        holder.rbAnswer.isChecked = true
                    }
                    holder.rbAnswer.setOnClickListener {
                        if (holder.rbAnswer.isChecked) {
                            //añade a la lista la opcion seleccionada
                            if (checkListTestDetalle.tipo == "OMU"){
                                respMarcadas?.clear()
                            }
                            respMarcadas!!.add(testOptions[position])
                        } else {
                            // si la opcion es deseleccionada, verifica si esta ya fue guardada en la lista de respuestas y la borra
                            if (respMarcadas != null && respMarcadas!!.contains(testOptions[position])) {
                                respMarcadas!!.remove(testOptions[position])
                            }
                        }
                        checkListTestDetalle.setRespuestaSeleccionada(respMarcadas!!)
                        listener.onRowItemClick(checkListTestDetalle.respuestaSeleccionada, checkListTestDetalle)
                    }
                } else {
                    //historico
                    holder.rbAnswer.isClickable = false
                    if (respMarcadas!!.contains(holder.rbAnswer.text.toString())) {
                        holder.rbAnswer.isChecked = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CATCH", Objects.requireNonNull(e.message).toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return testOptions!!.size
    }

    init {
        respMarcadas = ArrayList()
    }
}