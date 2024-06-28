package com.webcontrol.android.data.model

data class WorkerGMP(
        val id: Int,
        val nombres: String,
        val apellidos: String,
        val fechaNacimiento: String,
        val correo: String,
        val sexo: String,
        val idEstado: Int
) {}