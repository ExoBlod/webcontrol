package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ReserveCourseRequest
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getNiceDate
import kotlinx.android.synthetic.main.row_reservacurso_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservaCursoAdapter(
    private val mContext: Context,
    private val listReservasCurso: List<ReservaCurso>,
    var listener: ReservaCursoListAdapterListener
) : RecyclerView.Adapter<ReservaCursoAdapter.MyViewHolder>() {

    private lateinit var myView: View
    private var reservaCurso: ReservaCurso? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var lblCodigo: TextView = itemView.lblCodigo
        var lblTipo: TextView = itemView.lblTipo
        var lblDuracion: TextView = itemView.lblDuracion
        var lblLugar: TextView = itemView.lblLugar
        var lblSala: TextView = itemView.lblSala
        var lblCupos: TextView = itemView.lblCupos
        var lblDatetime: TextView = itemView.lblDatetime
        var btnReservar: Button = itemView.btnReservarCurso
        var containerCurso: ConstraintLayout = itemView.containerCurso


        override fun onClick(v: View) {
            reservaCurso = listReservasCurso[adapterPosition]
            listener.OnRowItemClick(reservaCurso)
        }


        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_reservacurso_list, parent, false)

        myView = itemView.rootView
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterPosition
        reservaCurso = listReservasCurso[position]

        holder.lblCodigo.text = "${reservaCurso!!.codeReserve} - ${reservaCurso!!.nameCourse}"
        holder.lblLugar.text = "Lugar: ${reservaCurso!!.place}"
        holder.lblSala.text = "Sala: ${reservaCurso!!.sala}"
        holder.lblDatetime.text =
            "Fecha: ${getNiceDate(reservaCurso!!.dateCourse)} ${reservaCurso!!.timeCourse}"
        holder.lblTipo.text =
            if (reservaCurso!!.required == "SI") "Tipo: Obligatorio" else "Tipo: Opcional"
        holder.lblCupos.text = "Cupos: ${reservaCurso!!.capacity}"
        holder.lblDuracion.text = "Duración: ${reservaCurso!!.duration} Hrs."
        if (reservaCurso!!.capacity != null) {
            if ((reservaCurso!!.capacity ?: 0) < 1) {
                holder.lblCupos.setTextColor(Color.RED)
                holder.btnReservar.visibility = View.INVISIBLE
            } else {
                holder.lblCupos.setTextColor(Color.parseColor("#163c5d"))
                holder.btnReservar.visibility = View.VISIBLE
            }
        }
        holder.btnReservar.setOnClickListener {
            reservaCurso = listReservasCurso[position]

            MaterialDialog.Builder(mContext)
                .title(R.string.alert_title)
                .content(R.string.question_reserve)
                .positiveText(R.string.buttonOk)
                .negativeText(R.string.buttonCancel)
                .autoDismiss(true)
                .onPositive { _, _ ->
                    reservar()
                }
                .onNegative { dialog, _ -> dialog.dismiss() }
                .show()
        }

        holder.containerCurso.setOnClickListener {
            reservaCurso = listReservasCurso[position]
            listener.OnRowItemClick(reservaCurso)
        }


    }

    override fun getItemCount(): Int {
        return listReservasCurso.size
    }

    interface ReservaCursoListAdapterListener {
        fun OnRowItemClick(reservaCurso: ReservaCurso?)
    }

    fun showMessageExistReserve() {
        MaterialDialog.Builder(mContext)
            .title(R.string.alert_title)
            .content(R.string.txt_reserve_not_found)
            .positiveText(R.string.buttonOk)
            .autoDismiss(true)
            .onPositive { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showMessageErrorReserve() {
        MaterialDialog.Builder(mContext)
            .title(R.string.alert_title)
            .content(R.string.txt_reserve_error)
            .positiveText(R.string.buttonOk)
            .autoDismiss(true)
            .onPositive { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showMessageSuccessReserve() {
        MaterialDialog.Builder(mContext)
            .title(R.string.alert_title)
            .content(R.string.txt_reserve_ok)
            .positiveText(R.string.buttonOk)
            .autoDismiss(true)
            .onPositive { _, _ ->
                myView.rootView
                val parentView = myView
                val navController = parentView.findNavController()
                navController.navigate(R.id.historialReservaCursoFragment)
            }
            .show()
    }

    private fun reservar() {
        val existReserve = App.db.reservaCursoDao().getOneByReserveMade(reservaCurso!!.codeReserve)
        if (existReserve != null) {
            showMessageExistReserve()
            return
        }
        val call: Call<ApiResponseAnglo<String>>
        val request = ReserveCourseRequest(
            reservaCurso!!.codeReserve,
            "${SharedUtils.getUsuarioId(mContext)}",
            "${SharedUtils.getIdCompany(mContext)}",
            "${SharedUtils.getUsuarioId(mContext)}"
        )

        val api = RestClient.buildAnglo()
        call = api.insertReserve(request)
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {

                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess && response.body()!!.data == "") {
                        showMessageSuccessReserve()
                    } else {
                        showMessageErrorReserve()
                    }
                } else {
                    showMessageErrorReserve()
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                showMessageErrorReserve()
            }
        })
    }
}