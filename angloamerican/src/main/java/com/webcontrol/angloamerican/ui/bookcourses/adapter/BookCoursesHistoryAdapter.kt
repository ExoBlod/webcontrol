package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.databinding.RowBookCoursesHistoryListBinding
import com.webcontrol.angloamerican.databinding.RowHistoryBookCoursesBinding
import java.text.SimpleDateFormat
import java.util.*

class BookCoursesHistoryAdapter(
    private var historyList: List<HistoryBookCourseData>,
    var buttonClickListener: OnButtonClickListener? = null
) :
    RecyclerView.Adapter<BookCoursesHistoryAdapter.BookCoursesHistoryHolder>() {

    fun setList(_historyList: List<HistoryBookCourseData>) {
        historyList = _historyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCoursesHistoryHolder {
        val itemBinding = RowHistoryBookCoursesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookCoursesHistoryHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookCoursesHistoryHolder, position: Int) {
        val paymentBean: HistoryBookCourseData = historyList[position]
        holder.bind(paymentBean, buttonClickListener)
    }

    override fun getItemCount(): Int = historyList.size

    interface OnButtonClickListener {
        fun onButtonClick(data: HistoryBookCourseData)
    }


    class BookCoursesHistoryHolder(val itemBinding: RowHistoryBookCoursesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            historyBookCourseData: HistoryBookCourseData,
            buttonClickListener: OnButtonClickListener? = null
        ) = with(itemBinding) {

            val isApproved = historyBookCourseData.Aprobo
            val status = if (isApproved == "SI") {
                "Aprobado"
            } else {
                "Reprobado"
            }

            if (status == "Reprobado") {
                lblState.setTextColor(Color.RED)
                txtResult.setTextColor(Color.RED)
                btnShowCertificate.isEnabled = false
            } else {
                btnShowCertificate.isEnabled = true
            }

            val formattedDate = historyBookCourseData.DateExam
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(formattedDate)
            val numQuestions = historyBookCourseData.NumPreguntas
            val resultadoResta =
                historyBookCourseData.NumPreguntas - historyBookCourseData.NumCorrectas
            val resultadoTexto = resultadoResta.toString()

            lblDate.text = outputFormat.format(date)
            txtCourseName.text = historyBookCourseData.Charla
            txtResult.text = historyBookCourseData.Nota.toString()
            txtQuestions.text = "Total Preguntas: $numQuestions"
            lblState.text = status
            txtCorrect.text = historyBookCourseData.NumCorrectas.toString()
            txtFailed.text = resultadoTexto
            val txtCorrectValue = historyBookCourseData.NumCorrectas
            val txtFailedValue = resultadoResta
            val certificate = historyBookCourseData.Url

            val total = txtCorrectValue + txtFailedValue

            if (total != 0) {
                val progressCorrect = (txtCorrectValue.toFloat() / total.toFloat() * 100).toInt()
                val progressFail = (txtFailedValue.toFloat() / total.toFloat() * 100).toInt()
                seekBarCorrect.progress = progressCorrect
                seekBarFail.progress = progressFail
            } else {
                seekBarCorrect.progress = 0
                seekBarFail.progress = 0
            }

            btnShowCertificate.setOnClickListener {
                val certificateLink = certificate

                val intent = Intent(Intent.ACTION_VIEW)
                intent.component = ComponentName("com.android.chrome", "com.google.android.apps.chrome.Main")
                intent.data = Uri.parse(certificateLink)

                try {
                    itemBinding.root.context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        itemBinding.root.context,
                        "Chrome no está instalado en el dispositivo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}