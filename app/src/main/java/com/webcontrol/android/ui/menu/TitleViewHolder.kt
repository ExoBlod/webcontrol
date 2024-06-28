package com.webcontrol.android.ui.menu

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.webcontrol.android.R

class TitleViewHolder(itemView: View, var adapter: NavMenuAdapter) : GroupViewHolder(itemView) {
    private val titleView: TextView = itemView.findViewById<View>(R.id.nav_menu_item_title) as TextView
    private var titleString: String? = null
    private val arrow: ImageView = itemView.findViewById<View>(R.id.nav_menu_item_arrow) as ImageView
    private val icon: ImageView = itemView.findViewById<View>(R.id.nav_menu_item_icon) as ImageView

    fun setTitleColor(color: Int) {
        titleView.setTextColor(color)
        icon.setColorFilter(color)
    }

    fun setGenreTitle(context: Context?, title: ExpandableGroup<*>) {
        if (title is TitleMenu) {
            if (title.imageResource != 0) {
                Glide.with(context!!)
                        .load(title.imageResource)
                        .into(icon)
            }
            titleString = title.getTitle()
            titleView.text = title.getTitle()
            if (title.items.size > 0) {
                arrow.visibility = View.VISIBLE
                var isExpand = false
                for (i in adapter.isExpandList.indices) {
                    if (titleString == adapter.isExpandList[i]) {
                        isExpand = true
                        break
                    }
                }
                if (isExpand) {
                    arrow.setImageResource(R.drawable.ic_keyboard_arrow_up)
                } else {
                    arrow.setImageResource(R.drawable.ic_keyboard_arrow_down)
                }
            } else {
                arrow.visibility = View.GONE
            }
        }
    }

    override fun expand() {
        adapter.isExpandList.add(titleString)
        adapter.notifyDataSetChanged()
    }

    override fun collapse() {
        adapter.isExpandList.remove(titleString)
        adapter.notifyDataSetChanged()
    }
}