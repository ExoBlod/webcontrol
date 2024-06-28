package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Alternativa
import com.webcontrol.android.data.model.CuestionarioResponse
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_dj_answer.view.*
import kotlinx.android.synthetic.main.row_question_txt.view.*

class OMAdapter(testListResponse: ArrayList<CuestionarioResponse>, val context:Context) : TestRecyclerViewAdapter<Alternativa>() {

    private var testList: ArrayList<CuestionarioResponse> = testListResponse

    companion object {
        private const val TAG = "OMAdapter"
        private const val OM = 0
        private const val OMR = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder = when (viewType) {
        OMR -> OMRViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_alternative_omr,
                parent,
                false
        ))
        OM -> OMViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_dj_answer,
                parent,
                false
        ))
        else -> throw IllegalArgumentException("Tipo de vista inválido: $viewType")
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int) = when (holder.itemViewType) {
        OM -> onBindOM(holder, position)
        OMR -> onBindOMR(holder, position)
        else -> throw IllegalArgumentException("BindViewHolder inválido: #$position")
    }

    private fun onBindOM(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as OMViewHolder
        myHolder.setUpView(alternativa = getItem(position))
    }

    inner class OMViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnAnswer: MaterialButton = view.btn_answer

        fun setUpView(alternativa: Alternativa?) {
            alternativa?.let {
                btnAnswer.text = it.descripcion
                btnAnswer.setOnClickListener {v ->
                    subItemClickListener?.onOMItemClick(adapterPosition, v, it)
                }

                testList.find { response ->
                    response.codCuestionario == it.codCuestionario
                            && response.codAlternativa == it.codigo
                }
                        .let { found -> btnAnswer.isChecked = found != null }
            }
        }
    }

    private fun onBindOMR(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as OMRViewHolder
        myHolder.setUpView(alternativa = getItem(position))
    }

    inner class OMRViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnAnswer: MaterialButton = view.btn_answer
        private val lblAnswer: EditText = view.lbl_answer

        fun setUpView(alternativa: Alternativa?) {
            alternativa?.let {
                btnAnswer.text = it.descripcion
                testList.find { response ->
                    response.codCuestionario == it.codCuestionario
                            && response.codAlternativa == it.codigo
                }
                        .let { found -> btnAnswer.isChecked = found != null  }
                lblAnswer.visibility = if (btnAnswer.isChecked) View.VISIBLE else View.GONE

                btnAnswer.addOnCheckedChangeListener { compoundButton, _ ->
                    lblAnswer.visibility = if (compoundButton.isChecked) View.VISIBLE else View.GONE
                }

                lblAnswer.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        subItemClickListener?.onOMTextChanged(adapterPosition, it, s.toString())
                    }
                })

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val cuestionario = getItem(position)
        cuestionario?.let {
            return if (it.comenta) OMR else OM
        }
        return -1
    }
}