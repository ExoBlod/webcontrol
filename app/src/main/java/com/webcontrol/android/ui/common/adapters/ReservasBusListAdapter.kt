package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.ReservaBus
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.row_historico_reservabus_list.view.*

class ReservasBusListAdapter(
        private val mContext: Context,
        private val listReservasBus: List<ReservaBus>,
        var listener: ReservasBusListAdapterListener
) : RecyclerView.Adapter<ReservasBusListAdapter.MyViewHolder>() {

    private var reservaBus: ReservaBus? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var lblOrigen: TextView = itemView.text_origen_name
        var lblDestino: TextView =itemView.text_destino_name
        var lblDate: TextView = itemView.text_date
        var lblTime: TextView = itemView.text_time
        var cardViewContainer: MaterialCardView= itemView.rowCard
        override fun onClick(v: View) {
            reservaBus = listReservasBus[adapterPosition]
            listener.OnRowItemClick(reservaBus)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_historico_reservabus_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        reservaBus = listReservasBus[position]
        holder.lblOrigen.text = reservaBus!!.origenName
        holder.lblDestino.text = reservaBus!!.destinoName
        val date = SharedUtils.getNiceDate(reservaBus!!.fecha)
        holder.lblDate.text = date
        holder.lblTime.text = reservaBus!!.hora
        holder.cardViewContainer.setOnClickListener {
            reservaBus = listReservasBus[position]
            listener.OnRowItemClick(reservaBus)
        }
    }

    override fun getItemCount(): Int {
        return listReservasBus.size
    }

    interface ReservasBusListAdapterListener {
        fun OnRowItemClick(reservaBus: ReservaBus?)
    }
}