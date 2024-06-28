package com.webcontrol.android.ui.covid.declaracion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DJConsolidado
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.row_preacceso_historico.view.*

class DJListAdapter(val cliente: String, val context: Context): TestRecyclerViewAdapter<DJConsolidado>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_preacceso_historico, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setUpView(getItem(position))
    }

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private val consolidadoTitle: TextView = view.txt_division
        private val consolidadoFecha: TextView = view.txt_fecha
        private val icon: ImageView = view.icon_status

        init {
            view.setOnClickListener(this)
        }

        fun setUpView(consolidado: DJConsolidado?) {
            consolidadoTitle.text = if (cliente == Constants.CLIENTE_ANGLO)
                                        context.getString(R.string.test_covid_title_qv)
                                    else
                                        context.getString(R.string.test_covid_title_anta)
            icon.visibility = View.GONE

            consolidado?.let {
                consolidadoFecha.text = if (cliente == Constants.CLIENTE_ANGLO)
                                            SharedUtils.getCustomDateFormat(
                                                it.fecha,
                                                "yyyy-MM-dd",
                                                "dd-MM-yyyy")
                                        else
                                            SharedUtils.getNiceDate(it.fecha)

            }

        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition, v)
        }
    }
}