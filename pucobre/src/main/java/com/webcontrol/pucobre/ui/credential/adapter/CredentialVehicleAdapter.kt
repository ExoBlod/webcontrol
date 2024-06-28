package com.webcontrol.pucobre.ui.credential.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.pucobre.R
import com.webcontrol.pucobre.data.model.VehicleList

class CredentialVehicleAdapter (private val vehicleList: ArrayList<VehicleList>):RecyclerView.Adapter<CredentialVehicleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialVehicleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return CredentialVehicleViewHolder(
            layoutInflater.inflate(
                R.layout.credential_back_vehicle,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CredentialVehicleViewHolder, position: Int) {
        val item = vehicleList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = vehicleList.size

}