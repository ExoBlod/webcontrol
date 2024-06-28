package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.CredentialBackVehicle
import com.webcontrol.angloamerican.databinding.CredentialBackVehicleBinding

class CredentialVehicleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: CredentialBackVehicleBinding = CredentialBackVehicleBinding.bind(view)

    fun render(vehicle: CredentialBackVehicle) {
        "-${vehicle.vehicleType}".also { binding.tvVehicle.text = it }
    }
}