package com.webcontrol.android.common

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.BR

class GenericViewHolder<T : Any>(
    private val binding: ViewDataBinding,
    private val clickListener: ((T) -> Unit)? = null
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var currentItem: T

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(item: T) {
        currentItem = item
        binding.setVariable(BR.obj, item)
        binding.executePendingBindings()
    }

    override fun onClick(v: View?) {
        clickListener?.invoke(currentItem)
    }
}
