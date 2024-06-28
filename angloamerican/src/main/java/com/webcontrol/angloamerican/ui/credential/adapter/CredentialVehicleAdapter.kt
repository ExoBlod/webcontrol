package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CredentialBackVehicle

class CredentialVehicleAdapter(private val listVehicle: ArrayList<CredentialBackVehicle>) :
    RecyclerView.Adapter<CredentialVehicleViewHolder>() {

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
        val item = listVehicle[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = listVehicle.size

}