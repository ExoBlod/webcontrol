package com.webcontrol.pucobre.ui.credential.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.pucobre.R
import com.webcontrol.pucobre.data.model.VehicleList
import com.webcontrol.pucobre.databinding.CredentialBackVehicleBinding
import com.webcontrol.pucobre.ui.credential.PucobreCredentialFragment
import kotlinx.android.synthetic.main.credential_back_vehicle.*


class CredentialVehicleViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val binding : CredentialBackVehicleBinding = CredentialBackVehicleBinding.bind(view)

    fun render(vehicle: VehicleList) {
        "-${vehicle.vehicleType}".also { binding.tvVehicle.text = it }
    }
}