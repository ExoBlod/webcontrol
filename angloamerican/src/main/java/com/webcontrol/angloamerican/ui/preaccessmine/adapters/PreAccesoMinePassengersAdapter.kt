package com.webcontrol.angloamerican.ui.preaccessmine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.databinding.RowPasajerosBinding

class PreAccesoMinePassengersAdapter(
    private var preaccesoDetalleList: MutableList<PreaccesoDetalleMina>,
    private val buttonClickListener: OnButtonClickListener
) :
    RecyclerView.Adapter<PreAccesoMinePassengersAdapter.MyViewHolder>() {

    val rutsMostrados = mutableListOf<String>()
    fun addPreAccessDetailMine(preaccesoDetalleMina: PreaccesoDetalleMina){
        preaccesoDetalleList.add(preaccesoDetalleMina)
        notifyItemInserted(preaccesoDetalleList.size)
    }

    interface OnButtonClickListener {
        fun onButtonClick(data: PreaccesoDetalleMina)
    }

    class MyViewHolder(private  val itemBinding : RowPasajerosBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(preaccesoDetalleMina: PreaccesoDetalleMina, buttonClickListener: OnButtonClickListener, rutsMostrados: MutableList<String>) = with(itemBinding){
            nombre .text = preaccesoDetalleMina.nombreWorker
            rut .text = preaccesoDetalleMina.rut
            if (preaccesoDetalleMina.isAutor && preaccesoDetalleMina.isValidated) {
                iconState.setImageResource(R.drawable.ic_check_circle_green_24dp)
            } else {
                iconState.setImageResource(R.drawable.ic_cancel_red_24dp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = RowPasajerosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = preaccesoDetalleList[position]
        holder.bind(item, buttonClickListener, rutsMostrados)
        rutsMostrados.add(item.rut)
    }

    override fun getItemCount(): Int {
        return preaccesoDetalleList.size
    }
}