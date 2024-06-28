package com.webcontrol.android.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DocumentLaPoderosa
import com.webcontrol.android.util.DateUtils
import kotlinx.android.synthetic.main.item_documento.view.txtFechaDocumento
import kotlinx.android.synthetic.main.item_documento.view.txtNombreDocumento
import java.text.SimpleDateFormat
import java.util.Date


class DocumentAdapterPoderosa(private val dataSet: List<DocumentLaPoderosa>) :
    RecyclerView.Adapter<DocumentAdapterPoderosa.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDocument: TextView = view.txtNombreDocumento
        val tvDate: TextView = view.txtFechaDocumento

        init {
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_documento, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.tvDocument.context
        val item = dataSet[position]
        viewHolder.tvDocument.text = item.nombreDocumento
        if (item.fecha.isNullOrEmpty()) {
            if (item.estado.isNullOrEmpty())
                viewHolder.tvDate.text = "--"
            else
                viewHolder.tvDate.text = item.estado
        } else {
            val lista = ObtenerFecha(item.fecha)
            viewHolder.tvDate.text = lista[0]
            if (lista[1] == "SI")
                viewHolder.tvDate.setTextColor(context.resources.getColor(R.color.dateColorRed))
            else
                viewHolder.tvDate.setTextColor(context.resources.getColor(R.color.dateColorBlue))
        }

    }

    override fun getItemCount() = dataSet.size

    private fun ObtenerFecha(fecha: String): List<String> {

        val arregloFechaSend: MutableList<String> = ArrayList()
        var fechaFormateada = ""
        var fechaRojo = ""

        if (!fecha.isNullOrEmpty()) {
            fechaFormateada = DateUtils.getDateOfString(fecha, "-")
            fechaRojo = DateUtils.getDateFormat(fecha, "yyyyMMdd")
        }

        val formatter = SimpleDateFormat("yyyyMMdd")
        val fechaAux: Date = formatter.parse(fechaRojo)
        val dateNow = Date()
        arregloFechaSend.add(fechaFormateada)
        if (fechaAux < dateNow)
            arregloFechaSend.add("SI")
        else
            arregloFechaSend.add("NO")


        return arregloFechaSend
    }

}