package com.webcontrol.android.ui.preacceso

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.databinding.FragmentPreaccesoDetalleHistoricoBinding
import com.webcontrol.android.ui.common.adapters.PreaccesoPasajerosAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PreaccesoDetalleHistoricoActivity : AppCompatActivity() {
    private lateinit var binding: FragmentPreaccesoDetalleHistoricoBinding
    private var preaccesoDetalleList: List<PreaccesoDetalle>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var pasajerosAdapter: PreaccesoPasajerosAdapter? = null
    private var id = 0
    private var preaccesoDetalle: PreaccesoDetalle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPreaccesoDetalleHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.fragment_preacceso_detalle_historico)
        load()
        preaccesoDetalleList = ArrayList()
        layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.listaPreaccesoDetalle.addItemDecoration(divider)
        binding.listaPreaccesoDetalle.layoutManager = layoutManager
        val extras = intent.extras
        if (extras != null) {
            id = extras.getInt(PREACCESO_ID)
        }
        preaccesoDetalle = PreaccesoDetalle()

        binding.btnRegresar.setOnClickListener {
            regresar()
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        preaccesoDetalleList = App.db.preaccesoDetalleDao().getAllByPreaccesoId(id)
        if (preaccesoDetalleList!!.isNotEmpty()) {
            pasajerosAdapter = PreaccesoPasajerosAdapter(preaccesoDetalleList!!, this)
            binding.listaPreaccesoDetalle.adapter = pasajerosAdapter
            binding.txtNumeroRegistros.text = preaccesoDetalleList!!.size.toString()
            binding.listaPreaccesoDetalle.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
        } else {
            binding.emptyState!!.visibility = View.VISIBLE
            binding.listaPreaccesoDetalle.visibility = View.GONE
        }
    }

    fun regresar() {
        finish()
    }

    companion object {
        const val PREACCESO_ID = "PREACCESO_ID"
    }
}