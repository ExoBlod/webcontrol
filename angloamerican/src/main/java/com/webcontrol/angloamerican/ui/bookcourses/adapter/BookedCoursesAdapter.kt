package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.BookedCoursesData
import com.webcontrol.angloamerican.databinding.RowBookedCoursesBinding
import com.webcontrol.angloamerican.utils.convertirFecha
import java.text.SimpleDateFormat
import java.util.*

class BookedCoursesAdapter(
    private var historyList: List<BookedCoursesData>,
    var buttonClickListener: OnButtonClickListener? = null
) :
    RecyclerView.Adapter<BookedCoursesAdapter.BookedCoursesHistoryHolder>() {

    fun setList(_historyList: List<BookedCoursesData>) {
        historyList = _historyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedCoursesHistoryHolder {
        val itemBinding = RowBookedCoursesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookedCoursesHistoryHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookedCoursesHistoryHolder, position: Int) {
        val paymentBean: BookedCoursesData = historyList[position]
        holder.bind(paymentBean, buttonClickListener)
    }

    override fun getItemCount(): Int = historyList.size

    interface OnButtonClickListener {
        fun onButtonClick(data: BookedCoursesData)
    }


    class BookedCoursesHistoryHolder(val itemBinding: RowBookedCoursesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            bookedCoursesData: BookedCoursesData,
            buttonClickListener: OnButtonClickListener? = null
        ) = with(itemBinding) {

            val charlaName = bookedCoursesData.CharlaName
            val firstCharlaName = bookedCoursesData.CharlaName[0]
            val date = bookedCoursesData.EndDate
            val formatDate = convertirFecha(date)

            txtCourseName.text = charlaName
            txtCapitalLetter.text = firstCharlaName.toString()

            val actualDateHour = Date()
            val dateFormat = SimpleDateFormat("yyyyMMdd")
            val dateHour = SimpleDateFormat("HH:mm")
            val startDateFormat = dateFormat.parse(bookedCoursesData.StartDate)
            val startHourFormat = dateHour.parse(bookedCoursesData.StartHour)
            val initDate =
                "${bookedCoursesData.StartDate[6]}${bookedCoursesData.StartDate[7]}/${bookedCoursesData.StartDate[4]}${bookedCoursesData.StartDate[5]}/${bookedCoursesData.StartDate[0]}${bookedCoursesData.StartDate[1]}${bookedCoursesData.StartDate[2]}${bookedCoursesData.StartDate[3]}"
            val initHour = "${bookedCoursesData.StartHour}"
            val startTime = Date(
                startDateFormat.year,
                startDateFormat.month,
                startDateFormat.date,
                startHourFormat.hours,
                startHourFormat.minutes
            )
            val fechaActualEsMayor = actualDateHour > startTime
            if (bookedCoursesData.Validity != 1 || !fechaActualEsMayor) {
                lblState.text = itemBinding.root.context.resources.getString(R.string.not_available)
                lblState.setTextColor(Color.RED)
                lblDate.text =
                    "Curso No Disponible"
            } else {
                lblState.text = itemBinding.root.context.resources.getString(R.string.available)
                lblDate.text = "Disponible hasta $formatDate"
            }

            messageContainer.setOnClickListener {
                if (lblState.text == "No Disponible") {
                    showAlert()
                } else {
                    buttonClickListener?.onButtonClick(bookedCoursesData)
                }
            }
        }

        private fun showAlert() {
            val alertDialogBuilder = AlertDialog.Builder(itemBinding.root.context)
            alertDialogBuilder.setTitle(itemBinding.root.context.resources.getString(R.string.not_available))
            alertDialogBuilder.setMessage(itemBinding.root.context.resources.getString(R.string.not_available_alert))
            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            }
            alertDialogBuilder.show()
        }
    }
}