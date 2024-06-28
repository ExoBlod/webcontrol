package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.databinding.FragmentTipoChecklistBinding
import com.webcontrol.android.databinding.RowHistoricoHeaderCheckListBinding
import com.webcontrol.android.util.SharedUtils.getNiceDate
import kotlinx.android.synthetic.main.row_historico_header_check_list.view.*
import java.util.*

class HistoricoCheckListAdapter(private val mContext: Context, private val listCheckList: List<CheckListTest>, var listener: HistoricoCheckListAdapterListener) : RecyclerView.Adapter<HistoricoCheckListAdapter.MyViewHolder>() {
    private var checkListTest: CheckListTest? = null
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var lblDivision: TextView = itemView.lblDivision
        var lblLocal: TextView = itemView.lblLocal
        var iconStatus : ImageView =itemView.iconStatus
        var iconName: ImageView =itemView.icon_name
        var lblFechaHora: TextView = itemView.lblFecha
        var lblHora: TextView = itemView.lblHora
        var messageContainer: ConstraintLayout =itemView.message_container
        var lblPatente: TextView = itemView.lblPatente

        override fun onClick(v: View) {
            checkListTest = listCheckList[adapterPosition]
            listener.OnRowItemClick(checkListTest)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MyViewHolder {
        val binding = LayoutInflater.from(parent.context)
                .inflate(R .layout.row_historico_header_check_list, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        checkListTest = listCheckList[position]
        if (checkListTest!!.tipoTest == "ENC" || checkListTest!!.tipoTest == "EQV") {
            holder.lblDivision.text = checkListTest!!.titulo
            holder.lblLocal.text = "ENCUESTA"
            holder.lblLocal.setPadding(0, 16, 0, 0)
            holder.lblPatente.visibility = View.GONE
        } else if (checkListTest!!.tipoTest == "TMZ") {
            holder.lblDivision.text = checkListTest!!.titulo
            holder.lblLocal.text = "TAMIZAJE"
            holder.lblLocal.setPadding(0, 16, 0, 0)
            holder.lblPatente.visibility = View.GONE
        } else {
            holder.lblPatente.text = if (checkListTest!!.vehicleId != null) checkListTest!!.vehicleId else ""
            holder.lblDivision.text = if (checkListTest!!.divisionName != null) checkListTest!!.divisionName else ""
            holder.lblLocal.text = if (checkListTest!!.localName != null) checkListTest!!.localName else ""
        }
        val generator = ColorGenerator.MATERIAL
        val color = generator.getColor(if (checkListTest!!.divisionId != null) checkListTest!!.divisionId else 8812853)
        val drawable = TextDrawable.builder().buildRound(
                if (!holder.lblDivision.text.isNullOrEmpty())
                    holder.lblDivision.text.toString().toUpperCase(Locale.getDefault()).substring(0, 1)
                else "W"
                , color)
        holder.iconName.setImageDrawable(drawable)
        holder.lblFechaHora.text = getNiceDate(checkListTest!!.fechaSubmit)
        holder.lblHora.text = checkListTest!!.horaSubmit
        holder.iconStatus.setImageResource(R.drawable.ic_cloud_done_black_24dp)
        holder.messageContainer.setOnClickListener {
            checkListTest = listCheckList[position]
            listener.OnRowItemClick(checkListTest)
        }
        if (checkListTest!!.estadoInterno == 2) {
            holder.iconStatus.setImageResource(R.drawable.ic_cloud_done_black_24dp)
        } else {
            holder.iconStatus.setImageResource(R.drawable.ic_cloud_off_black_24dp)
        }
    }

    override fun getItemCount(): Int {
        return listCheckList.size
    }

    interface HistoricoCheckListAdapterListener {
        fun OnRowItemClick(checkListTest: CheckListTest?)
    }
}