package com.webcontrol.android.ui.menu

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.webcontrol.android.R
import java.util.*

class NavMenuAdapter(private val context: Context, groups: List<ExpandableGroup<*>>, activity: Activity) : ExpandableRecyclerViewAdapter<TitleViewHolder, SubTitleViewHolder>(groups) {
    private val mListener: MenuItemClickListener
    var selectedItemParent = ""
    var selectedItemChild = ""
    var isExpandList = ArrayList<String?>()
    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.nav_menu_item, parent, false)
        val holder = TitleViewHolder(view, this)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): SubTitleViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.nav_submenu_item, parent, false)
        val holder = SubTitleViewHolder(view)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindChildViewHolder(holder: SubTitleViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>, childIndex: Int) {
        val menu = group as TitleMenu
        val subTitle = menu.items[childIndex]
        if (selectedItemChild == subTitle.name) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.navSubmenuSelected))
            //holder.setTitleColor(ContextCompat.getColor(context, R.color.navSubmenuSelectedText));
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.menu_selected_background)
        } else holder.itemView.setBackgroundColor(Color.WHITE)
        holder.setSubTitletName(subTitle.name)
        if (subTitle.count != 0) {
            holder.setCount(subTitle.count)
        }
        holder.itemView.setOnClickListener {
            selectedItemParent = menu.title
            selectedItemChild = subTitle.name
            mListener.onMenuItemClick(subTitle.name)
            notifyDataSetChanged()
        }
    }

    override fun onBindGroupViewHolder(holder: TitleViewHolder, flatPosition: Int, group: ExpandableGroup<*>) {
        val menu = group as TitleMenu
        if (selectedItemParent == menu.title) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.navSubmenuSelected))
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.menu_selected_background)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.setTitleColor(ContextCompat.getColor(context, R.color.primaryText))
        }
        holder.setGenreTitle(context, menu)
        if (menu.items.size < 1) {
            holder.itemView.setOnClickListener {
                selectedItemParent = menu.title
                selectedItemChild = ""
                mListener.onMenuItemClick(menu.title)
                notifyDataSetChanged()
            }
        } else if (menu.title.equals("Las Bambas")) {
            // Agregar el diálogo de actualización aquí
            holder.itemView.setOnClickListener {
                // Mostrar el diálogo de actualización de la aplicación
                mListener.onMenuItemClick("Las Bambas")
            }
        }
    }

    interface MenuItemClickListener {
        fun onMenuItemClick(itemString: String)
    }

    init {
        mListener = activity as MenuItemClickListener
    }
}