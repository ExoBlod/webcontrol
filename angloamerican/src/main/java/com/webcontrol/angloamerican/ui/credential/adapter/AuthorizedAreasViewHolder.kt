package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.AuthorizedAreas


class AuthorizedAreasViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val authArea = view.findViewById<TextView>(R.id.txtAuthZone)
    fun render(authAreas: AuthorizedAreas) {
        authArea.text = authAreas.authArea
    }
}