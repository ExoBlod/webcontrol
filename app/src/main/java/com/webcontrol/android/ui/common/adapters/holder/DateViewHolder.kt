package com.webcontrol.android.ui.common.adapters.holder

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.ui.checklist.CheckListEditTextListener
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener
import kotlinx.android.synthetic.main.row_historico_check_list.view.*
import kotlinx.android.synthetic.main.row_question_date.view.*
import kotlinx.android.synthetic.main.row_question_date.view.lblTestTitle
import kotlinx.android.synthetic.main.row_question_date.view.lbl_answer
import kotlinx.android.synthetic.main.row_question_txt.view.*
import java.text.SimpleDateFormat
import java.util.*

class DateViewHolder(val view: View,val context: Context,val checkListEditTextListener: CheckListEditTextListener, private val buttonsDisabled: Boolean) : BetterViewHolder<CheckListTest_Detalle>(view) {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_question_date
    }
    private val testAnswer: EditText = view.lbl_answer
    private val calendar = Calendar.getInstance()

    init {
        testAnswer.addTextChangedListener(checkListEditTextListener)
        testAnswer.setOnClickListener {
            DatePickerDialog(context, setOnDatePickListener(testAnswer, calendar),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun bind(item: CheckListTest_Detalle) {
        val testDescripcion: String = item.descripcion!!
        view.lblTestTitle.text = testDescripcion
        checkListEditTextListener.item = item
        view.lbl_answer.setText(item.respuestaSeleccionada)
        if(buttonsDisabled)
            view.lbl_answer.isEnabled = false
    }

    private fun setOnDatePickListener(inputView: EditText, calendar: Calendar) : DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth  ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(dateFormat, Locale.US)

            inputView.setText(sdf.format(calendar.time))
        }
    }
}