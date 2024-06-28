package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName
import com.webcontrol.android.ui.common.adapters.TypeFactory
import com.webcontrol.android.ui.common.adapters.Visitable

data class Antecedentes(
        @SerializedName("CodAntecedente")
        val codigo: Int,
        @SerializedName("Descripcion")
        val descripcion: String? = null,
        @SerializedName("Comentario")
        var comentario: String? = null,
        @SerializedName("Activo")
        val activo: String? = null,
        @SerializedName("Tipo")
        val tipo: String? = null,
        @SerializedName("Checked")
        var isChecked: Boolean? = false,
        @SerializedName("Comenta")
        var comenta: Boolean? = false

) : Visitable {
    override fun type(typeFactory: TypeFactory): Int {
        return typeFactory.type(this)
    }
}