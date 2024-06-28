package com.webcontrol.angloamerican.ui.credential.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.CredentialListCourses
import com.webcontrol.angloamerican.databinding.PopupCoursesInformationBinding
import com.webcontrol.angloamerican.databinding.RowCredentialCoursesBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CredentialListcoursesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val bindingCourses: RowCredentialCoursesBinding = RowCredentialCoursesBinding.bind(view)

    @SuppressLint("SimpleDateFormat")
    fun getCourses(credentialListCourses: CredentialListCourses) {
        with(bindingCourses) {
            val fechaVencimiento = credentialListCourses.FechaVencimiento
            val inputFormat = SimpleDateFormat("yyyyMMdd")
            val date = inputFormat.parse(fechaVencimiento)
            val currentDate = Date()

            val diffMillis = date.time - currentDate.time
            val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

            if (diffDays < 0) {
                tvStateCourse.text = "Vencido"
            } else if (diffDays <= 30) {
                tvStateCourse.text = "Por vencer"
            } else {
                tvStateCourse.visibility = View.GONE
            }

            val outputFormat = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = outputFormat.format(date)
            tvCourseName.text = credentialListCourses.Charla
            tvEndDate.text = formattedDate
        }
    }
}