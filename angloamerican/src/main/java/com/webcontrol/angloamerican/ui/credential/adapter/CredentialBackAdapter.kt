package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CredentialBack

class CredentialBackAdapter(private val credentialBack: ArrayList<CredentialBack>) :
    RecyclerView.Adapter<CredentialBackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialBackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return CredentialBackViewHolder(
            layoutInflater.inflate(
                R.layout.credential_back_item,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: CredentialBackViewHolder, position: Int) {
        val item = credentialBack[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = credentialBack.size

}