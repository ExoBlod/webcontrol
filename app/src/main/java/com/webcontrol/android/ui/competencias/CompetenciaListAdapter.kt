package com.webcontrol.android.ui.competencias

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Competencia
import kotlinx.android.synthetic.main.row_competencia.view.*

class CompetenciaListAdapter(private val items: List<Competencia>, val context: Context) :
        RecyclerView.Adapter<CompetenciaListAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.row_competencia,
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

        fun bind(item: Competencia, context: Context) {
            val adapter = CompetenciaDocListAdapter(item.documentos!!, context)
            itemView.lblCompetencia.text = item.descripcion
            itemView.rvsDocuments.layoutManager = LinearLayoutManager(context)
            itemView.rvsDocuments.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}