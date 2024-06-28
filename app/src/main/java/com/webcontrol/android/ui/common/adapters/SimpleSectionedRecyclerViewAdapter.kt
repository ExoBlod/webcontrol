package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import java.util.*

class SimpleSectionedRecyclerViewAdapter(context: Context, sectionResourceId: Int, textResourceId: Int,
                                         private val baseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext: Context = context
    private var mValid = true
    private val mSectionResourceId: Int = sectionResourceId
    private val mTextResourceId: Int = textResourceId
    private val mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mBaseAdapter = baseAdapter
    private val mSections = SparseArray<Section?>()

    class SectionViewHolder(view: View, mTextResourceid: Int) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById<View>(mTextResourceid) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, typeView: Int): RecyclerView.ViewHolder {
        return if (typeView == SECTION_TYPE) {
            val view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false)
            SectionViewHolder(view, mTextResourceId)
        } else {
            mBaseAdapter.onCreateViewHolder(parent, typeView - 1)
        }
    }

    override fun onBindViewHolder(sectionViewHolder: RecyclerView.ViewHolder, position: Int) {
        if (isSectionHeaderPosition(position)) {
            (sectionViewHolder as SectionViewHolder).title.text = mSections[position]!!.title
        } else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeaderPosition(position)) SECTION_TYPE else mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1
    }

    class Section(var firstPosition: Int, var title: CharSequence) {
        var sectionedPosition = 0

    }

    fun setSections(sections: Array<Section>) {
        mSections.clear()
        Arrays.sort(sections) { o, o1 -> if (o.firstPosition == o1.firstPosition) 0 else if (o.firstPosition < o1.firstPosition) -1 else 1 }
        for ((offset, section) in sections.withIndex()) {
            section.sectionedPosition = section.firstPosition + offset
            mSections.append(section.sectionedPosition, section)
        }
        notifyDataSetChanged()
    }

    private fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION
        }
        var offset = 0
        for (i in 0 until mSections.size()) {
            if (mSections.valueAt(i)!!.sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }
        return sectionedPosition + offset
    }

    private fun isSectionHeaderPosition(position: Int): Boolean {
        return mSections[position] != null
    }

    override fun getItemId(position: Int): Long {
        return if (isSectionHeaderPosition(position)) {
            Int.MAX_VALUE - mSections.indexOfKey(position).toLong()
        } else {
            mBaseAdapter.getItemId(sectionedPositionToPosition(position))
        }
    }

    override fun getItemCount(): Int {
        return if (mValid) mBaseAdapter.itemCount + mSections.size() else 0
    }

    companion object {
        private const val SECTION_TYPE = 0
    }

    init {
        mBaseAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                mValid = mBaseAdapter.itemCount > 0
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }
}