package com.webcontrol.android.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.databinding.ActivityCambiarTelefonoBinding
import com.webcontrol.android.vm.CambiarTelefonoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CambiarTelefonoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCambiarTelefonoBinding
    var vm: CambiarTelefonoViewModel? = null
    var navController: NavController? = null
    var loader: MaterialDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarTelefonoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_cambiar_telefono)
        initViewModel()
        initView()

        binding.btnAccion.setOnClickListener {
            goNext()
        }
        binding.btnReenviar.setOnClickListener {
            reenviarCodigo()
        }
        binding.btnVolver.setOnClickListener{
            goBack()
        }
    }

    fun initViewModel() {
        vm = ViewModelProviders.of(this).get(CambiarTelefonoViewModel::class.java)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        vm!!.limpiarSteps()
        vm!!.verifyError.observe(this, Observer { o: Any? -> })
        vm!!.step1.observe(this, Observer { s: String? ->
            if (s != null) {
                loader!!.dismiss()
                if (s == "ok") {
                    navController!!.navigate(R.id.from_data_to_confirm)
                    binding.tvSubtitulo.text = getString(R.string.msj_cambio_telefono_confirm, vm!!.extension.value, vm!!.telefono.value)
                    binding.btnAccion.text = "Confirmar"
                    binding.btnReenviar.visibility = View.VISIBLE
                    vm!!.limpiarCod()
                } else if (s == "pendiente") loader!!.show() else Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
            }
        })
        vm!!.step2.observe(this, Observer { s: String? ->
            if (s != null) {
                loader!!.dismiss()
                if (s == "ok") {
                    Toast.makeText(this, "Teléfono actualizado correctamente", Toast.LENGTH_LONG).show()
                    finish()
                } else if (s == "pendiente") loader!!.show() else if (s == "ok-reenvio") {
                    loader!!.dismiss()
                    Toast.makeText(this, "Código reenviado correctamente", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initView() {
        loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .build()
    }
    
    fun goBack() {
        when (Objects.requireNonNull(navController!!.currentDestination)?.id) {
            R.id.cambiarTelefonoDatosFragment -> finish()
            R.id.cambiarTelefonoConfirmarFragment -> {
                navController!!.navigateUp()
                binding.tvSubtitulo.text = getString(R.string.msj_cambio_telefono)
                binding.btnAccion.text = getString(R.string.label_button_changepass_verify)
                binding.btnReenviar.visibility = View.INVISIBLE
            }
        }
    }

    fun reenviarCodigo() {
        vm!!.reenviarSolicitudCambioTelefono()
    }

    fun goNext() {
        when (Objects.requireNonNull(navController!!.currentDestination)?.id) {
            R.id.cambiarTelefonoDatosFragment -> if (vm!!.dataValida()) {
                loader!!.show()
                vm!!.enviarSolicitudCambioTelefono()
            }
            R.id.cambiarTelefonoConfirmarFragment -> if (vm!!.dataConfirmValida()) {
                loader!!.show()
                vm!!.enviarCodigoVerificacion()
            }
        }
    }
}