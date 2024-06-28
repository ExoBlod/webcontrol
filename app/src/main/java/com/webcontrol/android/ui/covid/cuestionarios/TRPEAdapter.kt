package com.webcontrol.android.ui.covid.cuestionarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.TRPE
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_alternative_trpe.view.*

class TRPEAdapter(): TestRecyclerViewAdapter<TRPE>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_alternative_trpe, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as MyViewHolder
        myHolder.setUpView(getItem(position))
    }


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val lugarLbl: TextView = view.trpe_lugar_lbl
        private val dateLbl: TextView = view.trpe_date_lbl

        fun setUpView(trpeModel: TRPE?) {
            trpeModel?.let {
                lugarLbl.text = it.lugar!!
                dateLbl.text = it.fechaRetorno
            }
        }
    }

}