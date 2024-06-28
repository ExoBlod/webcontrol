package com.webcontrol.android.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DocumentsBambas
import com.webcontrol.android.data.model.Vehiculo

class VehicleAdapter (private val dataSet: List<String>) :
    RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVehicle: TextView


        init {
            tvVehicle = view.findViewById(R.id.txtNameVehicle)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_vehicles, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.tvVehicle.text = item

    }

    override fun getItemCount() = dataSet.size


}