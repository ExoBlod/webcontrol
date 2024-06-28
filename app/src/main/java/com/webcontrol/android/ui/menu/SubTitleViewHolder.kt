package com.webcontrol.android.ui.menu

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.webcontrol.android.R
import com.webcontrol.android.util.SharedUtils.toTitleCase

class SubTitleViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val subTitleTextView: TextView = itemView.findViewById<View>(R.id.main_nav_submenu_item_title) as TextView
    private val countTextView: TextView = itemView.findViewById<View>(R.id.count) as TextView

    fun setSubTitletName(name: String?) {
        subTitleTextView.text = toTitleCase(name)
    }

    fun setCount(count: Int) {
        countTextView.text = count.toString()
    }
}