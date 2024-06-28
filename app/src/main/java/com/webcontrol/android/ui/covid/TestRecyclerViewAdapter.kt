package com.webcontrol.android.ui.covid

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.data.OnDJItemClickListener
import com.webcontrol.android.data.OnItemClickListener
import com.webcontrol.android.data.OnSubItemClickListener

abstract class TestRecyclerViewAdapter<T>:
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<T>? = ArrayList()
    protected var itemClickListener: OnItemClickListener? = null
    protected var subItemClickListener: OnSubItemClickListener? = null
    protected var djItemClickListener: OnDJItemClickListener? = null

    fun addItems(items: ArrayList<T>) {
        this.list?.addAll(items)
        reload()
    }

    fun clear() {
        this.list?.clear()
        reload()
    }

    fun getItem(position: Int): T? = this.list?.get(position)

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun setOnSubItemClickListener(onItemClickListener: OnSubItemClickListener) {
        this.subItemClickListener = onItemClickListener
    }

    fun setOnDJItemClickListener(onItemClickListener: OnDJItemClickListener) {
        this.djItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int = list!!.size

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }
}