package com.webcontrol.android.ui.common.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.util.SharedUtils.getNiceDate
import kotlinx.android.synthetic.main.row_historico_reservacurso_list.view.*


class HistoricoReservaCursoListAdapter(
    private val mContext: Context,
    private val listReservasCurso: List<ReservaCurso>,
    var listener: HistoricoReservaCursoListAdapterListener
) : RecyclerView.Adapter<HistoricoReservaCursoListAdapter.MyViewHolder>() {

    private var reservaCurso: ReservaCurso? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var lblCodigo: TextView =itemView.lblCodigo
        var lblFechaCurso: TextView = itemView.lblFechaCurso
        var lblFechaRes: TextView =itemView.lblFechaRes
        var lblHoraRes: TextView = itemView.lblHoraRes
        var lblTipo: TextView = itemView.lblTipo
        var lblDuracion: TextView = itemView.lblDuracion
        var iconStatus: ImageView = itemView.iconStatus
        var containerCurso: ConstraintLayout =itemView.containerHistory


        override fun onClick(v: View) {
            reservaCurso = listReservasCurso[adapterPosition]
            listener.OnRowItemClick(reservaCurso)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_historico_reservacurso_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        reservaCurso = listReservasCurso[position]
        holder.lblCodigo.text = "${reservaCurso!!.codeReserve} - ${reservaCurso!!.nameCourse}"
        holder.lblFechaCurso.text = "Fecha Curso: ${getNiceDate(reservaCurso!!.dateCourse)} ${reservaCurso!!.timeCourse}"
        holder.lblTipo.text = if(reservaCurso!!.required == "SI") "Tipo: OPCIONAL" else "Tipo: OPCIONAL"
        holder.lblFechaRes.text = getNiceDate(reservaCurso!!.dateReserve)
        holder.lblHoraRes.text = reservaCurso!!.timeReserve
        holder.lblDuracion.text = "Duracion: ${reservaCurso!!.duration} Hrs."

        holder.containerCurso.setOnClickListener {
            reservaCurso = listReservasCurso[position]
            listener.OnRowItemClick(reservaCurso)
        }

        if (reservaCurso!!.statusReserve == "R") {
            holder.iconStatus.setImageResource(R.drawable.ic_check_circle_green_24dp)
        } else {
            holder.iconStatus.setImageResource(R.drawable.ic_cancel_red_24dp)
        }
    }

    override fun getItemCount(): Int {
        return listReservasCurso.size
    }

    interface HistoricoReservaCursoListAdapterListener {
        fun OnRowItemClick(reservaCurso: ReservaCurso?)
    }
}