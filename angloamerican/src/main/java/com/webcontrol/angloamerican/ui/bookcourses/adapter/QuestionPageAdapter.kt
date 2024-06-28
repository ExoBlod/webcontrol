package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.databinding.RowItemPageBinding

class QuestionPageAdapter(
    private var numberPage: Int,
    var buttonClickListener: OnButtonClickListener? = null
) :
    RecyclerView.Adapter<QuestionPageViewHolder>() {

    fun setNumberPage(_numberPage: Int) {
        numberPage = _numberPage
        notifyDataSetChanged()
    }

    interface OnButtonClickListener {
        fun onItemClick(courseId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPageViewHolder {

        val itemBinding =
            RowItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionPageViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: QuestionPageViewHolder, position: Int) {
        holder.render(position, buttonClickListener)
    }

    override fun getItemCount(): Int = numberPage

}