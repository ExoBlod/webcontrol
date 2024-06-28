package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.AuthorizedAreas

class AuthorizedAreasAdapter(private val authZonesList:List<AuthorizedAreas>):RecyclerView.Adapter<AuthorizedAreasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorizedAreasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AuthorizedAreasViewHolder(layoutInflater.inflate(R.layout.item_auth_areas, parent, false))
    }

    override fun getItemCount(): Int {
        return authZonesList.size
    }

    override fun onBindViewHolder(holder: AuthorizedAreasViewHolder, position: Int) {
        val item = authZonesList[position]
        holder.render(item)
    }
}