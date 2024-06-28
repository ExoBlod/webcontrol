package com.webcontrol.android.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable

class GenericAdapter<T>(private var typeFactory: TypeFactory<T>) :
    RecyclerView.Adapter<GenericViewHolder<Visitable<T>>>() {

    private var items: List<Visitable<T>> = ArrayList()
    private var listener: ((Visitable<T>) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<Visitable<T>> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
        return GenericViewHolder(binding, listener)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type(typeFactory)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<Visitable<T>>, position: Int) {

        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Visitable<T>>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: ((Visitable<T>) -> Unit)?) {
        this.listener = listener
    }
}
