package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.ui.common.adapters.holder.BetterViewHolder

class ChecklistAdapter(
        val context: Context,
        val items: List<Visitable>,
        val typeFactory: TypeFactory,
        val buttonsDisabled: Boolean,
        val checklistType: String,
        val listener: CheckListAdapterListener,
        val checkListener: OnCheckListener?

) : RecyclerView.Adapter<BetterViewHolder<Visitable>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetterViewHolder<Visitable> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return typeFactory.holder(viewType, view, context, checklistType, buttonsDisabled, listener, checkListener) as BetterViewHolder<Visitable>
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type(typeFactory)
    }

    override fun onBindViewHolder(holder: BetterViewHolder<Visitable>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}