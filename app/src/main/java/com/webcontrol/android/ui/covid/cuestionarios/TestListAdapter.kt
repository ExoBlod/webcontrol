package com.webcontrol.android.ui.covid.cuestionarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.CuarentenaDetalle
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.row_preacceso_historico.view.*

class TestListAdapter: TestRecyclerViewAdapter<CuarentenaDetalle>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_preacceso_historico, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setUpView(cuarentenaDetalle = getItem(position))
    }

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private val consolidadoTitle: TextView = view.txt_division
        private val consolidadoFecha: TextView = view.txt_fecha
        private val icon: ImageView = view.icon_status

        init {
            view.setOnClickListener(this)
        }

        fun setUpView(cuarentenaDetalle: CuarentenaDetalle?) {
            consolidadoTitle.text = "Seguimiento Diario"
            icon.visibility = View.GONE

            cuarentenaDetalle?.let {
                consolidadoFecha.text = SharedUtils.getCustomDateFormat(it.fecha, "yyyyMMdd", "dd-MM-yyyy")
            }

        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition, v)
        }
    }
}