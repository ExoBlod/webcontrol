package com.webcontrol.android.common

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.network.dto.VehicleCategory
import com.webcontrol.android.data.network.dto.VehicleType

class VehiculoAdapter(private val dataSet: List<VehicleCategory>, private val context: Context) :
    RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtType: TextView
        val txtDescription: TextView
        val btnShow: Button

        init {
            txtType = view.findViewById(R.id.txtType)
            txtDescription = view.findViewById(R.id.txtDescription)
            btnShow = view.findViewById(R.id.btnShow)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_grid_vehiculos, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.txtType.text = dataSet[position].name
        viewHolder.txtDescription.text = getString(dataSet[position].vehicleList)

        viewHolder.btnShow.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_descripcion_vehiculos_autorizados)

            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
            titulo.text = dataSet[position].name

            var lista = dialog.findViewById<TextView>(R.id.txt_description)
            lista.text = getString(dataSet[position].vehicleList)
            dialog.show()
        }

    }

    override fun getItemCount() = dataSet.size

    private fun getString(listDescription: List<VehicleType>): String {
        var description: String = ""
        listDescription.forEach {
            description = "${it.name} \n $description"
        }
        return description
    }
}