package com.webcontrol.android.ui.competencias

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DocCompetencia
import kotlinx.android.synthetic.main.row_competencia_doc.view.*

class CompetenciaDocListAdapter(private val items: List<DocCompetencia>, val context: Context) :
        RecyclerView.Adapter<CompetenciaDocListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.row_competencia_doc,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: DocCompetencia, context: Context) {
            itemView.lblDocCompetencia.text = item.nombre
        }
    }
}