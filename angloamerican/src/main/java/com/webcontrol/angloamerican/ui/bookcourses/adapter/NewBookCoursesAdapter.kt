package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CourseData

class NewBookCoursesAdapter(
    private var list: List<CourseData>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<NewBookCoursesViewHolder>() {

    fun setList(_list: List<CourseData>) {
        list = _list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(courseId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBookCoursesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return NewBookCoursesViewHolder(
            layoutInflater.inflate(
                R.layout.row_new_book,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewBookCoursesViewHolder, position: Int) {
        val item = list[position]
        holder.render(item)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item.idProgram)
        }
    }

    override fun getItemCount(): Int = list.size

}