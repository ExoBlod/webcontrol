package com.webcontrol.angloamerican.ui.preaccessmine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.databinding.RowPreaccesoHistoricoBinding
import com.webcontrol.core.utils.SharedUtils

class PreAccesoMineAdapter (private var preaccesoList: List<PreaccesoMina>,private val buttonClickListener: OnButtonClickListener) :
    RecyclerView.Adapter<PreAccesoMineAdapter.MyViewHolder>() {

    fun setList(_historyList: List<PreaccesoMina>){
        preaccesoList = _historyList
        notifyDataSetChanged()
    }

    interface OnButtonClickListener {
        fun onButtonClick(data: PreaccesoMina)
    }

    class MyViewHolder(private val itemBinding: RowPreaccesoHistoricoBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(preacceso: PreaccesoMina, buttonClickListener: OnButtonClickListener) = with(itemBinding){
            val generator = ColorGenerator.MATERIAL
            val color = generator.getColor(preacceso.divisionNombre?:"DIV")
            val divisionName =  if(preacceso.divisionNombre.isNullOrEmpty()) "LB" else preacceso.divisionNombre!!
            val drawable = TextDrawable.builder()
                .buildRound(divisionName.toUpperCase().substring(0, 1), color)
            iconName.setImageDrawable(drawable)
            txtFecha.text = SharedUtils.getNiceDate(preacceso.fecha?:"SIN FECHA")
            txtDivision.text = preacceso.divisionNombre?:"SIN NOMBRE"
            txtLocal.text = preacceso.localNombre?:"SIN LOCAL"
            if ((preacceso.estado?:"NS") == "S") {
                iconStatus.setImageResource(R.drawable.ic_cloud_done_black_24dp)
            } else {
                iconStatus.setImageResource(R.drawable.ic_cloud_off_black_24dp)
            }
            itemBinding.view.setOnClickListener {
                buttonClickListener.onButtonClick(preacceso)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding = RowPreaccesoHistoricoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = preaccesoList[position]
        holder.bind(item,buttonClickListener)
    }

    override fun getItemCount(): Int {
        return preaccesoList.size
    }
}