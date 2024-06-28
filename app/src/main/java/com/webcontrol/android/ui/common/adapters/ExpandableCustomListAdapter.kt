package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Cursos
import com.webcontrol.android.data.db.entity.Examenes
import com.webcontrol.android.util.SharedUtils.getNiceDate

class ExpandableCustomListAdapter(private val _context: Context, private val listaCursos: List<Cursos>) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return listaCursos.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listaCursos[groupPosition].listExamenes.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return listaCursos[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listaCursos[groupPosition].listExamenes[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var rootView = convertView
        val curso = getGroup(groupPosition) as Cursos
        if (rootView == null) {
            val infalInflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rootView = infalInflater.inflate(R.layout.header, parent, false)
        }
        val lbl_NomCurso = rootView!!.findViewById<View>(R.id.lbl_NomCurso) as TextView
        val lblFechaHora = rootView.findViewById<View>(R.id.lblFechaHora) as TextView
        val lblHora = rootView.findViewById<View>(R.id.lblHora) as TextView
        val lbl_Orador = rootView.findViewById<View>(R.id.lbl_Orador) as TextView
        val lbl_Sistema = rootView.findViewById<View>(R.id.lblSistema) as TextView
        val img_ExpandCollapse = rootView.findViewById<View>(R.id.img_ExpandCollapse) as ImageView
        if (curso != null) {
            lbl_NomCurso.text = curso.nomCurso
            val fecha = curso.fechaHoraCurso!!.substring(0, 10).replace(" ", "").replace("-", "")
            val hora = curso.fechaHoraCurso!!.substring(11).replace(" ", "")
            lblFechaHora.text = getNiceDate(fecha)
            lblHora.text = hora
            lbl_Orador.text = curso.orador
            lbl_Sistema.text = curso.idSistema
            if (isExpanded) {
                lbl_NomCurso.setTypeface(null, Typeface.BOLD)
                img_ExpandCollapse.setImageResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                lbl_NomCurso.setTypeface(null, Typeface.NORMAL)
                img_ExpandCollapse.setImageResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
        return rootView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var rootView = convertView
        val examen = getChild(groupPosition, childPosition) as Examenes
        if (rootView == null) {
            val infalInflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rootView = infalInflater.inflate(R.layout.childs, parent, false)
        }
        val lbl_NomEvaluacion = rootView!!.findViewById<View>(R.id.lbl_NomEvaluacion) as TextView
        val lbl_Duracion = rootView.findViewById<View>(R.id.lbl_Duracion) as TextView
        val lbl_EstadoEvaluacion = rootView.findViewById<View>(R.id.lbl_EstadoEvaluacion) as TextView
        val img_EstadoEvaluacion = rootView.findViewById<View>(R.id.img_EstadoEvaluacion) as ImageView
        val img_ResultadoEvaluacion = rootView.findViewById<View>(R.id.img_ResultadoEvaluacion) as ImageView
        if (examen != null) {
            lbl_NomEvaluacion.text = examen.nomExamen
            when (examen.tipo) {
                "EXAMEN" -> {
                    val units = examen.tiempoTotal!!.split(":").toTypedArray()
                    val horas = units[0].toInt()
                    val minutos = units[1].toInt()
                    lbl_Duracion.text = (if (horas < 10) "0$horas" else horas).toString() + " hras " + (if (minutos < 10) "0$minutos" else minutos) + " min"
                }
                "ENCUESTA" -> lbl_Duracion.text = "-- hras -- min"
                else -> lbl_Duracion.text = "-- hras -- min"
            }
            var texto_estado = ""
            var color = Color.parseColor("#000000")
            when (examen.estado) {
                0 -> {
                    texto_estado = "Pendiente"
                    color = Color.parseColor("#FDD835")
                    img_EstadoEvaluacion.setImageResource(R.drawable.pending)
                }
                1 -> {
                    texto_estado = "Iniciado"
                    color = Color.parseColor("#00B0FF")
                    img_EstadoEvaluacion.setImageResource(R.drawable.inprogress)
                }
                2 -> {
                    texto_estado = "Terminado"
                    color = Color.parseColor("#00C853")
                    img_EstadoEvaluacion.setImageResource(R.drawable.completed)
                }
                else -> {
                    texto_estado = ""
                    color = Color.parseColor("#000000")
                    img_EstadoEvaluacion.setImageResource(R.drawable.pending)
                }
            }
            lbl_EstadoEvaluacion.text = texto_estado
            lbl_EstadoEvaluacion.setTextColor(color)
            when (examen.tipo) {
                "EXAMEN" -> if (examen.estado == 2) {
                    when (examen.aprobo) {
                        "SI" -> img_ResultadoEvaluacion.setImageResource(R.drawable.success)
                        "NO" -> img_ResultadoEvaluacion.setImageResource(R.drawable.failed)
                        else -> img_ResultadoEvaluacion.setImageResource(R.drawable.no_evaluated)
                    }
                } else img_ResultadoEvaluacion.setImageResource(R.drawable.no_evaluated)
                "ENCUESTA" -> if (examen.estado == 2) img_ResultadoEvaluacion.setImageResource(R.drawable.encuesta_terminada) else img_ResultadoEvaluacion.setImageResource(R.drawable.encuesta_pendiente)
                else -> img_ResultadoEvaluacion.setImageResource(R.drawable.encuesta_pendiente)
            }
        }
        return rootView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}