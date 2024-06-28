package com.webcontrol.angloamerican.ui.credential.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.CredentialBack
import com.webcontrol.angloamerican.databinding.CredentialBackItemBinding
import com.webcontrol.angloamerican.utils.Utils

class CredentialBackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: CredentialBackItemBinding = CredentialBackItemBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun render(credentialBack: CredentialBack) {
        with(binding) {
            tvOperation.text = "      " + credentialBack.operation + "      "
            tvActive.text = "   " +credentialBack.active + "   "
            tvDateDelivery.text = Utils.getFormatCredentialDate(credentialBack.deliveryDate)
            tvDateLic.text = Utils.getFormatCredentialDate(credentialBack.expirationDate)
        }
    }
}