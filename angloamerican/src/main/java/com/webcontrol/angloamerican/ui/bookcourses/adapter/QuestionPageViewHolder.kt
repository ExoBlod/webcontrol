package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.databinding.RowItemPageBinding

class QuestionPageViewHolder(val itemPageBinding: RowItemPageBinding) :
    RecyclerView.ViewHolder(itemPageBinding.root) {
    fun render(
        pageNumber: Int,
        buttonClickListener: QuestionPageAdapter.OnButtonClickListener? = null
    ) {
        "Pag${pageNumber + 1}".also {
            itemPageBinding.buttonItem.text = it
            itemPageBinding.buttonItem.setOnClickListener {
                buttonClickListener?.onItemClick(pageNumber)
            }
        }
    }
}