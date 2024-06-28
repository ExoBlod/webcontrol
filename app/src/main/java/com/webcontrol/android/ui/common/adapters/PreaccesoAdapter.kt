package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.ui.preacceso.PreaccesoDetalleHistoricoActivity
import com.webcontrol.android.util.SharedUtils.getNiceDate
import kotlinx.android.synthetic.main.row_preacceso_historico.view.*

class PreaccesoAdapter(private val mContext: Context, private val preaccesoList: List<Preacceso>) :
    RecyclerView.Adapter<PreaccesoAdapter.MyViewHolder>() {
    private var preacceso: Preacceso? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var txtFecha: TextView = itemView.txt_fecha
        var txtDivision: TextView = itemView.txt_division
        var txtLocal: TextView = itemView.txt_local
        var iconStatus: ImageView = itemView.icon_status
        var iconName: ImageView = itemView.icon_name

        override fun onClick(v: View) {
            preacceso = preaccesoList[adapterPosition]
            val intent = Intent(mContext, PreaccesoDetalleHistoricoActivity::class.java)
            intent.putExtra("PREACCESO_ID", preacceso!!.id)
            mContext.startActivity(intent)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_preacceso_historico, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        preacceso = preaccesoList[position]
        val generator = ColorGenerator.MATERIAL
        val color = generator.getColor(preacceso!!.divisionNombre)
        val drawable = TextDrawable.builder()
            .buildRound(preacceso!!.divisionNombre!!.toUpperCase().substring(0, 1), color)
        holder.iconName.setImageDrawable(drawable)
        holder.txtFecha.text = getNiceDate(preacceso!!.fecha)
        holder.txtDivision.text = preacceso!!.divisionNombre
        holder.txtLocal.text = preacceso!!.localNombre
        if (preacceso!!.estado == "S") {
            holder.iconStatus.setImageResource(R.drawable.ic_cloud_done_black_24dp)
        } else {
            holder.iconStatus.setImageResource(R.drawable.ic_cloud_off_black_24dp)
        }
    }

    override fun getItemCount(): Int {
        return preaccesoList.size
    }
}