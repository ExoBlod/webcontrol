package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.databinding.RowNewBookBinding

class NewBookCoursesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding: RowNewBookBinding = RowNewBookBinding.bind(view)

    fun render(courses: CourseData) {
        val capacity = courses.capacity.toInt()
        val amount = courses.amount.toInt()
        val avaibleSlots = capacity - amount
        courses.charlaName.also { binding.txtCourseName.text = it }
        "Inicia el ${courses.startDate}".also { binding.lblDate.text = it }
        "$avaibleSlots cupos libres".also { binding.lblState.text = it }
        "${courses.charlaName[0]}".also { binding.txtCapitalLetter.text = it }
    }
}