package com.webcontrol.angloamerican.ui.credential.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CredentialBackVehicle
import com.webcontrol.angloamerican.data.network.response.CredentialListCourses

class CredentialListCoursesAdapter(private val listCourses: ArrayList<CredentialListCourses>,
) :
    RecyclerView.Adapter<CredentialListcoursesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialListcoursesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return CredentialListcoursesViewHolder(
            layoutInflater.inflate(
                R.layout.row_credential_courses,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CredentialListcoursesViewHolder, position: Int) {
        val item = listCourses[position]
        holder.getCourses(item)
    }

    override fun getItemCount(): Int = listCourses.size
}