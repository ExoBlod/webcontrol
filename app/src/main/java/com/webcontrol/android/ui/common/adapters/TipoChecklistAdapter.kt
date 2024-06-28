package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Checklists
import kotlinx.android.synthetic.main.row_tipo_checklist.view.*

class TipoChecklistAdapter(private val mContext: Context, var checklistsList: List<Checklists>, var listener: TipoChecklistListener) : RecyclerView.Adapter<TipoChecklistAdapter.MyViewHolder>() {
    var checklists: Checklists? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_tipo_checklist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        checklists = checklistsList[position]
        holder.btnChecklist.text = checklists.toString()
        holder.btnChecklist.setOnClickListener {
            checklists = checklistsList[position]
            listener.onRowItemClick(checklists)
        }
    }

    override fun getItemCount(): Int {
        return checklistsList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var btnChecklist: Button = itemView.btn_checklist
        override fun onClick(view: View) {
            checklists = checklistsList[adapterPosition]
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface TipoChecklistListener {
        fun onRowItemClick(checklists: Checklists?)
    }
}