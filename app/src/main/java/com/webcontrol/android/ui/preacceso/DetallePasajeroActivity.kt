package com.webcontrol.android.ui.preacceso

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.databinding.FragmentDetallePasajeroBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils.FormatRut
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetallePasajeroActivity : AppCompatActivity() {

    private lateinit var binding: FragmentDetallePasajeroBinding
    private var id = 0
    private var preaccesoDetalle: PreaccesoDetalle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetallePasajeroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.fragment_detalle_pasajero)
        val extras = intent.extras
        if (extras != null) {
            id = extras.getInt(PREACCESO_DETALLE_ID)
            preaccesoDetalle = App.db.preaccesoDetalleDao().getOne(id)
        }
        var nombreCompleto = "%s %s"
        nombreCompleto = String.format(
            nombreCompleto,
            preaccesoDetalle!!.nombreWorker,
            preaccesoDetalle!!.apellidoWorker
        )
        binding.txtNombre.text = nombreCompleto
        binding.txtRut.text = FormatRut(preaccesoDetalle!!.rut)
        binding.txtEmpresa.text = preaccesoDetalle!!.companiaNombre
        binding.txtRutEmpresa.text = preaccesoDetalle!!.companiaId
        binding.txtContratista.text = preaccesoDetalle!!.ost
        binding.txtAutorizacion.text = preaccesoDetalle!!.detalleAutor
        binding.iconState.visibility = View.VISIBLE
        if (preaccesoDetalle!!.isAutor && preaccesoDetalle!!.isValidated) {
            binding.iconState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_check_circle_green_24dp
                )
            )
        } else {
            binding.iconState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_cancel_red_24dp
                )
            )
        }
        var url = "%sworker/profile/photo/%s"
        url = String.format(url, getString(R.string.ws_url_anglo), preaccesoDetalle!!.rut)
        GlideApp.with(this).load(url).placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp).circleCrop()
            .into(binding.imgProfile)
        binding.btnRegresar.setOnClickListener {
            regresar()
        }
    }

    fun regresar() {
        finish()
    }

    companion object {
        const val PREACCESO_DETALLE_ID = "PREACCESO_DETALLE_ID"
    }
}