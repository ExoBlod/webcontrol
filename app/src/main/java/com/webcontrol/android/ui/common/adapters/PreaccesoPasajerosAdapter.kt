package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.ui.preacceso.DetallePasajeroActivity
import com.webcontrol.android.util.SharedUtils.FormatRut
import kotlinx.android.synthetic.main.row_pasajeros.view.*

class PreaccesoPasajerosAdapter(private val preaccesoDetalleList: List<PreaccesoDetalle>, private val context: Context) : RecyclerView.Adapter<PreaccesoPasajerosAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var iconState: ImageView = itemView.icon_state
        var lblNombre: TextView = itemView.nombre
        var lblRut: TextView = itemView.rut
        var iconInfo: ImageView =itemView.icon_info

        override fun onClick(v: View) {
            val preaccesoDetalle = preaccesoDetalleList[adapterPosition]
            if (v.id == R.id.icon_info) {
                val intent = Intent(context, DetallePasajeroActivity::class.java)
                intent.putExtra("PREACCESO_DETALLE_ID", preaccesoDetalle.id)
                context.startActivity(intent)
            }
        }

        init {
            itemView.setOnClickListener(this)
            iconInfo.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_pasajeros, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pasajero = preaccesoDetalleList[position]
        holder.lblNombre.text = pasajero.nombreWorker
        holder.lblRut.text = FormatRut(pasajero.rut)
        if (pasajero.isAutor && pasajero.isValidated) {
            holder.iconState.setImageResource(R.drawable.ic_check_circle_green_24dp)
        } else {
            holder.iconState.setImageResource(R.drawable.ic_cancel_red_24dp)
        }
    }

    override fun getItemCount(): Int {
        return preaccesoDetalleList.size
    }
}