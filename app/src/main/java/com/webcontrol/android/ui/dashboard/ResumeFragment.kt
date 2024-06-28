package com.webcontrol.android.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.databinding.FragmentResumeBinding
import com.webcontrol.android.ui.checklist.HistoricoCheckListDetalleFragment
import com.webcontrol.android.ui.preacceso.PreaccesoDetalleHistoricoActivity
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.getNiceDate
import com.webcontrol.android.util.SharedUtils.getUsuario
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.showLoader
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ResumeFragment : Fragment() {
    private lateinit var binding: FragmentResumeBinding
    private var preacceso: Preacceso? = null
    private var idLastPreacceso = 0
    private var idLastFatiga = 0
    private var idLastVehiculo = 0
    private var preaccesoDetalle: PreaccesoDetalle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Resumen"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumeBinding.inflate(inflater, container, false)
        preacceso = Preacceso()
        preaccesoDetalle = PreaccesoDetalle()
        setIds()
        binding.includecheck.cardChecklistFatiga.setOnClickListener {
            onClickFatiga()
        }
        binding.includecheck.cardChecklistVehiculos.setOnClickListener {
            onClickVehiculo()
        }
        binding.includeuser.constraintCredential.setOnClickListener {
            verCredencial()
        }
        binding.includeuser.imgFuncionario.setOnClickListener {
            verCredencial()
        }
        binding.includeprea.constraintAllpassenger.setOnClickListener {
            verDetallePreacceso()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoader(context, getString(R.string.loading))
        setLastPreacceso()
        setChecklistFatiga()
        setChecklistVehiculo()
        setCredencial()
    }

    private fun setIds() {
        idLastPreacceso = App.db.preaccesoDao().lastInsertedId()
        idLastFatiga = App.db.checkListDao().getLastInsertedCheckListTestId("TFS")
        idLastVehiculo = App.db.checkListDao().getLastInsertedCheckListTestId("TDV")
    }

    fun onClickFatiga() {
        if (idLastFatiga > 0) {
            findNavController().navigate(
                R.id.historicoCheckListDetalleFragment,
                bundleOf(
                    HistoricoCheckListDetalleFragment.CHECKLIST_TYPE to "TFS",
                    HistoricoCheckListDetalleFragment.CHECKLIST_NAME to "Fatiga y Somnolencia",
                    HistoricoCheckListDetalleFragment.CHECKLIST_ID to "0$idLastFatiga"
                )
            )
        }
    }

    fun onClickVehiculo() {
        if (idLastVehiculo > 0) {
            findNavController().navigate(
                R.id.historicoCheckListDetalleFragment,
                bundleOf(
                    HistoricoCheckListDetalleFragment.CHECKLIST_TYPE to "TDV",
                    HistoricoCheckListDetalleFragment.CHECKLIST_NAME to "Vehiculos",
                    HistoricoCheckListDetalleFragment.CHECKLIST_ID to "0$idLastVehiculo"
                )
            )
        }
    }

    private fun setCredencial() {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(urlPhoto, getString(R.string.ws_url_mensajeria), getUsuarioId(context))
        GlideApp.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .into(binding.includeuser.imgFuncionario)
        binding.includeuser.lblName.text = getUsuario(context).uppercase(Locale.getDefault())
        dismissLoader(context)
    }
    fun verCredencial() {
        findNavController().navigate(R.id.angloamericanCredentialFragment)
    }

    fun setLastPreacceso() {
        if (idLastPreacceso > 0) {
            preacceso = App.db.preaccesoDao().getOne(idLastPreacceso)
            if (preacceso != null) {
                binding.includeprea.viewPreacceso.visibility = View.VISIBLE
                binding.includeprea.emptyState.visibility = View.GONE
                binding.includeprea.lblDivision.text = preacceso!!.divisionNombre
                binding.includeprea.lblLocalPreacceso.text = preacceso!!.localNombre
                binding.includeprea.lblDate.text = getNiceDate(preacceso!!.fecha)
                binding.includeprea.lblPatente.text = preacceso!!.patente
                if (preacceso!!.sentido == "IN") binding.includeprea.lblSentido.text = "INGRESO" else if (preacceso!!.sentido == "OUT") binding.includeprea.lblSentido.text = "SALIDA"
            }
        } else {
            binding.includeprea.emptyState.visibility = View.VISIBLE
            binding.includeprea.viewPreacceso.visibility = View.GONE
        }
    }

    fun verDetallePreacceso() {
        if (idLastPreacceso > 0) {
            val intent = Intent(context, PreaccesoDetalleHistoricoActivity::class.java)
            intent.putExtra("PREACCESO_ID", idLastPreacceso)
            requireContext().startActivity(intent)
        }
    }

    private fun setChecklistFatiga() {
        if (idLastFatiga > 0) {
            val checkListTest = App.db.checkListDao().selectCheckListById(idLastFatiga)
            if (checkListTest != null) {
                val checklistTestDetail =
                    App.db.checkListDao().selectDetalleByChecked(checkListTest.idDb, true)
                if (checklistTestDetail != null) binding.includecheck.cardChecklistFatiga.setIconState(R.drawable.ic_cancel_red_24dp) else binding.includecheck.cardChecklistFatiga.setIconState(
                    R.drawable.ic_check_circle_green_24dp
                )
                binding.includecheck.cardChecklistFatiga.setIconStateVisibility(View.VISIBLE)
                binding.includecheck.cardChecklistFatiga.setSubtitle(checkListTest.divisionName.toString())
                binding.includecheck.cardChecklistFatiga.setTextDate(getNiceDate(checkListTest.fechaSubmit))
            }
        }
    }

    private fun setChecklistVehiculo() {
        if (idLastVehiculo > 0) {
            val checkListVehiculo = App.db.checkListDao().selectCheckListById(idLastVehiculo)
            if (checkListVehiculo != null) {
                val checkListTestDetalle =
                    App.db.checkListDao().selectDetalleByChecked(checkListVehiculo.idDb, false)
                if (checkListTestDetalle != null) binding.includecheck.cardChecklistVehiculos.setIconState(R.drawable.ic_cancel_red_24dp) else binding.includecheck.cardChecklistVehiculos.setIconState(
                    R.drawable.ic_check_circle_green_24dp
                )
                binding.includecheck.cardChecklistVehiculos.setIconStateVisibility(View.VISIBLE)
                binding.includecheck.cardChecklistVehiculos.setSubtitle(checkListVehiculo.divisionName.toString())
                binding.includecheck.cardChecklistVehiculos.setTextDate(getNiceDate(checkListVehiculo.fechaSubmit))
            }
        }
    }

    companion object {
        const val title = "title"
    }
}