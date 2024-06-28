package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Capacitaciones
import com.webcontrol.android.util.SharedUtils

class CapacitacionesAdapter(
    private val mContext: Context,
    private val listCapacitaciones: List<Capacitaciones>,
    var listener: CapacitacionesAdapterListener
) : RecyclerView.Adapter<CapacitacionesAdapter.MyViewHolder>() {

    private lateinit var myView: View
    private var capacitaciones: Capacitaciones? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var lblNombre: TextView = itemView.findViewById(R.id.lblNombreCurso)
        var lblCod: TextView = itemView.findViewById(R.id.lblCod)
        var lblNota: TextView = itemView.findViewById(R.id.lblNota)
        var lblFecha: TextView = itemView.findViewById(R.id.lblDatetime)
        var simbolo: ImageView = itemView.findViewById(R.id.imageC)
        var button:Button= itemView.findViewById(R.id.btnInfo)

        override fun onClick(v: View) {
            capacitaciones = listCapacitaciones[adapterPosition]
            listener.onRowItemClick(capacitaciones)
        }
        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_capacitaciones_list, parent, false)

        myView = itemView.rootView
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        capacitaciones = listCapacitaciones[position]

        holder.lblNombre.text =capacitaciones?.charla
        //holder.lblCod.text = capacitaciones?.idCharla
        holder.lblFecha.text = "Fecha vencimiento: "+ SharedUtils.getNiceDate(capacitaciones?.vencimiento)
        holder.lblNota.text = "Nota: ${capacitaciones?.nota}"


        if(capacitaciones?.aprobo=="APROBADO")
        {
            holder.simbolo.setImageResource(R.drawable.ic_check_circle_green_24dp)
        }
        else {
            holder.simbolo.setImageResource(R.drawable.ic_cancel_red_24dp)
        }


        holder.button.setOnClickListener {
            capacitaciones = listCapacitaciones[position]
            listener.onRowItemClick(capacitaciones)
        }

    }

    override fun getItemCount(): Int {
        return listCapacitaciones.size
    }

    interface CapacitacionesAdapterListener {
        fun onRowItemClick(capacitaciones: Capacitaciones?)
    }
}