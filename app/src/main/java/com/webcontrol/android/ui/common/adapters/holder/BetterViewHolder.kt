package com.webcontrol.android.ui.common.adapters.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BetterViewHolder<in T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
}