package com.webcontrol.android.ui.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.ChecklistGroups
import com.webcontrol.android.data.model.WorkerPase
import com.webcontrol.android.databinding.FragmentTestCheckListBinding
import com.webcontrol.android.ui.common.adapters.*
import com.webcontrol.android.ui.preacceso.CabeceraActivity
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getCompanyCheckList
import com.webcontrol.android.util.SharedUtils.getNiceDate
import com.webcontrol.android.util.SharedUtils.getOSTCheckList
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.util.SharedUtils.wCDate
import com.webcontrol.android.vm.CheckListTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CheckListTestFragment : Fragment(), CheckListAdapterListener, IOnBackPressed,
    OnCheckListener {
    private lateinit var binding: FragmentTestCheckListBinding
    val checkListTestViewModel: CheckListTestViewModel by viewModels()
    private var linearLayoutManager: LinearLayoutManager? = null
    private val checkListTestAdapter: CheckListTestAdapter? = null
    private var checkListTestm: CheckListTest? = null
    private var loader: MaterialDialog? = null
    private var TipoCheckList: String? = null
    private var NomCheckList: String? = null
    private var idChecklist: String? = null
    private var IdCompany: String? = ""
    private var questionsList: MutableList<CheckListTest_Detalle>? = null
    private var pass = true
    private var aproboTPA = false
    private var esPasajero = false
    private var rutPasajero: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoCheckList = requireArguments().getString("TipoCheckList")
        NomCheckList = requireArguments().getString("NomCheckList")
        idChecklist = requireArguments().getString("idChecklist")
        rutPasajero = requireArguments().getString(ARG_PARAM4)
        if (!rutPasajero.isNullOrEmpty())
            esPasajero = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestCheckListBinding.inflate(inflater, container, false)
        binding.lblFecha.text = getNiceDate(SharedUtils.wCDate)
        binding.lblHora.text = SharedUtils.time
        IdCompany = SharedUtils.getIdCompany(context)
        binding.btnEnviarResultados.setOnClickListener {
            EnviarResultados()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecycler()
        loadData()
        //This is the code to provide a sectioned list
        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> = ArrayList()
        checkListTestViewModel.getCheckListResult()
            .observe(viewLifecycleOwner, Observer { checkListTest ->
                if (checkListTest != null) {
                    val idcheckListLast =
                        App.db.checkListDao().getLastInsertedCheckListTestId(TipoCheckList)
                    checkListTestm = App.db.checkListDao().selectCheckListById(idcheckListLast)
                    checkListTestm!!.grupoList = checkListTest.grupoList
                    binding.lblNomTest.text = checkListTestm!!.titulo
                    var numPreguntas = 0
                    questionsList = ArrayList<CheckListTest_Detalle>()
                    for (i in checkListTestm!!.grupoList.indices) {
                        val preguntas =
                            checkListTestm!!.grupoList[i].preguntas.sortedBy { it.idTest_Detalle }
                        val groups = ChecklistGroups()
                        groups.idDb = checkListTestm!!.idDb
                        groups.checklistTypeId = checkListTestm!!.tipoTest
                        groups.checklistId = checkListTestm!!.idTest
                        groups.checklistGroupId = checkListTestm!!.grupoList[i].checklistGroupId
                        groups.groupName = checkListTestm!!.grupoList[i].groupName
                        groups.groupType = checkListTestm!!.grupoList[i].groupType
                        groups.groupMessage = checkListTestm!!.grupoList[i].groupMessage
                        groups.preguntas = checkListTestm!!.grupoList[i].preguntas
                        App.db.checklistGroupsDao().insert(groups)
                        for (j in preguntas.indices) {
                            val item = CheckListTest_Detalle()
                            item.idDb = preguntas[j].idDb
                            item.idTest = preguntas[j].idTest
                            item.idTest_Detalle = preguntas[j].idTest_Detalle
                            item.tipo = preguntas[j].tipo
                            item.title = preguntas[j].title
                            item.descripcion = preguntas[j].descripcion
                            item.isChecked = preguntas[j].isChecked
                            item.groupId = checkListTestm!!.grupoList[i].checklistGroupId
                            item.respuestas = preguntas[j].respuestas
                            item.typeCheckList = TipoCheckList
                            questionsList!!.add(item)
                        }
                        sections.add(
                            SimpleSectionedRecyclerViewAdapter.Section(
                                numPreguntas,
                                checkListTestm!!.grupoList[i].groupName!!
                            )
                        )
                        numPreguntas += checkListTestm!!.grupoList[i].preguntas.size
                    }
                    binding.lblNumPreguntas.text = "$numPreguntas Preguntas"
                    //checkListTestAdapter = new CheckListTestAdapter(getContext(), questionsList, CheckListTestFragment.this, false, TipoCheckList, this);
                    val typeFactory: TypeFactory = TypeFactoryImpl()
                    val adapter = ChecklistAdapter(
                        requireContext(),
                        questionsList!!,
                        typeFactory,
                        false,
                        TipoCheckList!!,
                        this,
                        this
                    )
                    val dummy =
                        arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
                    val mSectionedAdapter = SimpleSectionedRecyclerViewAdapter(
                        requireContext(),
                        R.layout.section,
                        R.id.section_text,
                        adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
                    )
                    mSectionedAdapter.setSections(sections.toTypedArray())
                    binding.rcvTestList.adapter = mSectionedAdapter
                    if (TipoCheckList == "DDS") {
                        binding.btnEnviarResultados.let {
                            it.text = getString(R.string.finalizar)
                        }
                    }
                }
                loader!!.dismiss()
            })

        checkListTestViewModel.getCheckListError()
            .observe(viewLifecycleOwner, Observer { result ->
                loader!!.dismiss()
                Log.e(TAG, result.toString())
                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar") { v: View? -> loadData() }.show()
            })


        checkListTestViewModel.getCheckListEnvioResult()
            .observe(viewLifecycleOwner, Observer { result ->
                if (result && TipoCheckList == "TPA") {
                    if (aproboTPA && !esPasajero)
                        startActivity(Intent(context, CabeceraActivity::class.java))
                    else {
                        MaterialDialog.Builder(requireContext())
                            .title("Notice")
                            .content("No podrá continuar por que es un posible caso de COVID19")
                            .cancelable(false)
                            .positiveText("Aceptar")
                            .onPositive { dialog, which ->
                                App.db.checkListDao()
                                    .updateCheckListTestSend(checkListTestm!!.idDb, 2, wCDate, time)
                                findNavController().navigate(
                                    R.id.historicoCheckListFragment, bundleOf(
                                        HistoricoCheckListFragment.CHECKLIST_TYPE to TipoCheckList,
                                        HistoricoCheckListFragment.CHECKLIST_NAME to NomCheckList,
                                        HistoricoCheckListFragment.CHECKLIST_ID to idChecklist,
                                        HistoricoCheckListFragment.COMPANY_ID to IdCompany
                                    )
                                )
                            }
                            .show()
                    }
                } else if (result || TipoCheckList == "COV" || TipoCheckList == "DDS") {
                    App.db.checkListDao()
                        .updateCheckListTestSend(checkListTestm!!.idDb, 2, wCDate, time)
                    findNavController().navigate(
                        R.id.historicoCheckListFragment, bundleOf(
                            HistoricoCheckListFragment.CHECKLIST_TYPE to TipoCheckList,
                            HistoricoCheckListFragment.CHECKLIST_NAME to NomCheckList,
                            HistoricoCheckListFragment.CHECKLIST_ID to idChecklist,
                            HistoricoCheckListFragment.COMPANY_ID to IdCompany
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo completar la operación intentelo nuevamente",
                        Toast.LENGTH_LONG
                    ).show()
                }
                //loader.dismiss();
                ProgressLoadingJIGB.finishLoadingJIGB(context)
            })

        checkListTestViewModel.getCheckListEnvioError()
            .observe(viewLifecycleOwner, Observer { result ->
                MaterialDialog.Builder(requireContext())
                    .title("Error")
                    .content("No se pudo completar la operación intentelo nuevamente")
                    .cancelable(false)
                    .positiveText("Aceptar")
                    .show()
                loader!!.dismiss()
                ProgressLoadingJIGB.finishLoadingJIGB(context)
            })

        checkListTestViewModel.blockPassResult.observe(
            viewLifecycleOwner,
            Observer { result: Boolean ->
                pass = if (result || TipoCheckList == "COV") {
                    true
                    //Toast.makeText(getContext(), "Su pase fue bloqueado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo completar la operación intentelo nuevamente",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
                loader!!.dismiss()
            })

        checkListTestViewModel.getBlockPassError().observe(viewLifecycleOwner, Observer { s ->
            loader!!.dismiss()
            Toast.makeText(
                context,
                "No se pudo bloquear su pase, intentelo nuevamente",
                Toast.LENGTH_LONG
            ).show()
            pass = false
        })

        if (TipoCheckList == "COV") {
            try {
                var flagCdl: Boolean?
                var msgCdl: String?
                checkListTestViewModel.CdlResponse.observe(
                    viewLifecycleOwner,
                    Observer<List<String>> {
                        flagCdl = it[0] != "SI"
                        msgCdl = it[1]
                        if (flagCdl != null && msgCdl != null)
                            ShowCdlDialog(flagCdl!!, msgCdl!!)
                    })
            } catch (e: Exception) {
                Log.e("GetWorkerData", "Error al cargar datos: " + e.message)
            }

        }
    }

    fun checkTestComplete(): Boolean {
        for (g in checkListTestm!!.grupoList) {
            for (p in g.preguntas) {
                when (p.tipo) {
                    "SN" -> {
                        if (!p.btnSIActive && !p.isBtnNOActive) {
                            return false
                        }
                    }

                    "OMU" -> {
                        if (p.respuestaSeleccionada.isNullOrEmpty())
                            return false
                    }

                    "DATE" -> {
                        if (p.idTest_Detalle == 2 && p.respuestaSeleccionada.isNullOrEmpty())
                            return false
                    }
                }
            }
        }
        return true
    }

    fun revisarRespondidos(): Boolean {
        var flag: Boolean = false
        for (g in checkListTestm!!.grupoList) {
            for (p in g.preguntas) {
                if (p.tipo == "SN") {
                    if (p.respuestaSeleccionada == "" && TipoCheckList != "TMZ") {
                        //para no TMZ
                    } else {
                        if (!p.btnSIActive) {  //verificar si hay uno q no esta respondido
                            flag = true
                            return flag
                        }
                    }

                }
            }
        }
        return false
    }

    fun mensajeResumen(): Array<String> {
        var varSN: Array<String> = arrayOf("NO", "NO")
        var si: Int = 0
        var no: Int = 0
        for (g in checkListTestm!!.grupoList) {
            for (p in g.preguntas) {
                if (p.tipo == "SN") {
                    if (p.isChecked == true) {
                        si++
                    } else {
                        no++
                    }
                }
            }
        }
        varSN.set(0, si.toString())
        varSN.set(1, no.toString())
        return varSN
    }

    fun EnviarResultados() {
        if (TipoCheckList.equals("COV") && CDLAnswersReview())
            alertasEnviarResultados()
        //resumen respuestas de TAMIZAJE
        if (!checkTestComplete() && TipoCheckList == "DDS") {
            MaterialDialog.Builder(requireContext())
                .title("Alerta")
                .content("Responda todas las preguntas")
                .positiveText("Aceptar")
                .autoDismiss(true)
                .show()

        } else if (revisarRespondidos() && TipoCheckList == "TMZ") {
            MaterialDialog.Builder(requireContext())
                .title("Alerta")
                .content("Responda todas las preguntas")
                .positiveText("Aceptar")
                .autoDismiss(true)
                .show()

        } else if (TipoCheckList == "TMZ") {
            MaterialDialog.Builder(requireContext())
                .title("Resumen Respuestas")

                .content(
                    "Marque SI confirmar y NO para modificar:" + '\n' +
                            // + for (vm in checkListTestm ){} +
                            "Respuestas:" + '\n' +
                            "SI (" + mensajeResumen()[0] + ") " + '\n' +
                            "NO (" + mensajeResumen()[1] + ")"
                )
                .cancelable(false)
                .positiveText("SI")
                .onPositive { dialog, which ->
                    print("revisarRespondidos::::" + revisarRespondidos())
                    alertasEnviarResultados()

                }
                .negativeText("NO")
                .show()
        } else {
            if (!TipoCheckList.equals("COV")){
                if (ischeckAnyBlankResponses()) {
                    MaterialDialog.Builder(requireContext())
                        .title(getString(R.string.alert))
                        .content(getString(R.string.complete_answer))
                        .positiveText(getString(R.string.text_ok))
                        .autoDismiss(true)
                        .show()
                    binding.btnEnviarResultados.visibility = View.GONE
                } else {
                    alertasEnviarResultados()
                }

            }
        }
    }
    fun ischeckAnyBlankResponses (): Boolean {
        return checkListTestm?.grupoList?.any { grupo ->
            grupo.preguntas.any { pregunta ->
                pregunta.valorChecked == ""
            }
        } == true
    }

    fun CDLAnswersReview(): Boolean {
        var flag = true
        for (g in checkListTestm!!.grupoList) {
            for (p in g.preguntas) {
                if (p.tipo == "") {
                    if ((p.btnSIActive && !p.isBtnNOActive) || (p.isBtnNOActive && !p.btnSIActive)) {
                        //verificar si hay uno q no esta respondido
                    } else
                        flag = false
                } else {
                    if (p.respuestaSeleccionada.isNullOrBlank())
                        flag = false
                }
            }
        }
        return flag
    }

    fun alertasEnviarResultados() {
        var tituloAlerta = "Warning"
        var mensajeFinal =
            "Puede ingresar a Faena\n\nRECUERDE: \nSIN PERJUICIO QUE LAS MANTENCIONES Y/O REVISIONES PERIÓDICAS SEAN EFECTUADAS POR EL CONDUCTOR U OTRO SEGÚN LA FRECUENCIA DEFINIDA, ES OBLIGACIÓN DEL CONDUCTOR VERIFICAR AL INICIO DE CADA VIAJE EL BUEN FUNCIONAMIENTO DEL VEHÍCULO, DETENIENDO DE MANERA INMEDIATA SI DETECTARA ALGUNA CAUSA DE “Detención inmediata” acorde a esta lista de verificación"
        if (isOnline(requireContext())) {
            if (TipoCheckList == "DDS") {
                tituloAlerta = getString(R.string.atencion_titulo)
                mensajeFinal =
                    "TODOS LOS DATOS EXPRESADOS EN ESTA FICHA CONSTITUYEN DECLARACIÓN JURADA DE MI PARTE. HE SIDO INFORMADO QUE DE OMITIR O FALSEAR INFORMACIÓN PUEDO PERJUDICAR MI PROPIA SALUD Y LA DE MIS COMPAÑEROS, LO CUAL ATENTA CONTRA LA SALUD PÚBLICA, CONSTITUYENDO TAMBIÉN UNA FALTA GRAVE POR LO QUE ASUMO LAS CONSECUENCIAS DE CUALQUIER OMISIÓN DE INFORMACIÓN."
            } else if (TipoCheckList == "TFS") {
                checkListTestm?.email = SharedUtils.get_email(requireContext())
                mensajeFinal =
                    "Recuerde, si usted presenta alguno de los estados asociados en la lista de verificación NO CONDUZCA, DÉ AVISO DE INMEDIATO A SU SUPERVISOR."
            } else if (TipoCheckList == "ENC" || TipoCheckList == "EQV") {
                mensajeFinal = "Enviar encuesta?"
            } else if (TipoCheckList == "TMZ") {
                mensajeFinal = "¿Enviar tamizaje?"
            } else if (TipoCheckList == "COV") {
                mensajeFinal = "¿Enviar encuesta?"
                checkListTestm?.email = SharedUtils.get_email(requireContext())
            } else if (TipoCheckList == "TPA") {
                checkListTestm?.email = SharedUtils.get_email(requireContext())
                mensajeFinal = "¿Enviar encuesta?"
            } else {
                TipoCheckList = "TDV"
                NomCheckList = "Vehículos"
                idChecklist = "1"
                checkListTestm?.email = SharedUtils.get_email(requireContext())
            }
            MaterialDialog.Builder(requireContext())
                .title(tituloAlerta)
                .content(mensajeFinal)
                .positiveText("ACEPTAR")
                .negativeText("CANCELAR")
                .autoDismiss(true)
                .onPositive { dialog, which ->
                    if (checkListTestm != null) {
                        if (TipoCheckList == "TMZ") {
                            val date = SharedUtils.wCDate
                            SharedUtils.setTmzNow(context, date)
                        }
                        if (checkListTestm!!.grupoList.size > 0) {
                            checkListTestm!!.fechaSubmit = SharedUtils.wCDate
                            checkListTestm!!.horaSubmit = SharedUtils.time
                            checkListTestm!!.ost = getOSTCheckList(requireContext())
                            checkListTestm!!.companyId=(getCompanyCheckList(requireContext()))
                            if (TipoCheckList == "ENC") {
                                if (isBlocked) {
                                    val workerPase = WorkerPase()
                                    workerPase.WorkerId = getUsuarioId(context)
                                    workerPase.Reason = getString(R.string.reason_coronavirus_test)
                                    workerPase.State = "BLOQUEADO"
                                    workerPase.Date = SharedUtils.wCDate
                                    workerPase.Time = SharedUtils.time
                                    checkListTestViewModel.blockPass(workerPase)
                                }
                            }
                            if (TipoCheckList == "TPA") {
                                val preguntas = checkListTestm!!.grupoList[0].preguntas
                                if (preguntas[0].isChecked && preguntas[1].isChecked)
                                    aproboTPA = true
                                if (esPasajero) {
                                    checkListTestm!!.workerId = rutPasajero
                                }
                            }

                            if (isOnline(requireContext())) {
                                if (pass) {
                                    ProgressLoadingJIGB.startLoadingJIGB(
                                        context,
                                        R.raw.loaddinglottie,
                                        "Cargando...",
                                        0,
                                        500,
                                        200
                                    )
                                    if (TipoCheckList == "DDS") {
                                        if (SharedUtils.getLocationCoordinates(requireContext())
                                                .isNotEmpty()
                                        )
                                            checkListTestm!!.location =
                                                SharedUtils.getLocationCoordinates(requireContext())
                                        SharedUtils.setLocationCoordinates(requireContext(), "")
                                        var message =
                                            "¿Esta seguro que desea finalizar la encuesta?"
                                        var respuestaNegativa = false
                                        checkListTestm?.grupoList?.get(1)?.preguntas?.forEach {
                                            if (it.isChecked) {
                                                respuestaNegativa = true
                                            }
                                        }
                                        if (respuestaNegativa) {
                                            message += "\n USTED PRESENTA UNO O MAS SINTOMAS SE NOTIFICARÁ AL ÁREA MÉDICA Y TENDRÁ QUE PERMANECER EN SU HABITACIÓN O RESIDENCIA"
                                        }
                                        MaterialDialog.Builder(requireContext())
                                            .title("COMPLETADO!")
                                            .content(message)
                                            .positiveText("ACEPTAR")
                                            .negativeText("CANCELAR")
                                            .cancelable(false)
                                            .autoDismiss(true)
                                            .onPositive { dialog, which ->
                                                checkListTestViewModel.sendCheckListTest(
                                                    checkListTestm
                                                )
                                            }
                                            .onNegative { dialog, which ->
                                                ProgressLoadingJIGB.finishLoadingJIGB(requireContext())
                                                dialog.dismiss()
                                            }
                                            .show()
                                    } else if (TipoCheckList != "COV")
                                        if (IdCompany == Companies.KRS.valor) {
                                            checkListTestViewModel.sendCheckListTestKrs(
                                                checkListTestm
                                            )
                                        } else {
                                            checkListTestViewModel.sendCheckListTest(
                                                checkListTestm
                                            )
                                        }
                                    else
                                        checkListTestViewModel.sendCheckListTestCOV(
                                            checkListTestm,
                                            IdCompany
                                        )
                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        "No se pudo enviar",
                                        Snackbar.LENGTH_LONG
                                    ).setAction("Reintentar") { EnviarResultados() }.show()
                                }
                            } else {
                                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                                    .setAction("Reintentar") { EnviarResultados() }.show()
                            }
                        } else Toast.makeText(
                            context,
                            "No hay preguntas disponibles",
                            Toast.LENGTH_LONG
                        ).show()
                    } else Toast.makeText(
                        context,
                        "No hay preguntas disponibles",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .onNegative { dialog, which -> dialog.dismiss() }
                .show()
        } else showToast(requireContext(), "No hay conexion a internet. Inténtelo mas tarde")
    }


    protected fun initializeRecycler() {
        val divider: ItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        linearLayoutManager = LinearLayoutManager(context)
        binding.rcvTestList.setHasFixedSize(true)
        binding.rcvTestList.layoutManager = linearLayoutManager
        binding.rcvTestList.addItemDecoration(divider)
    }

    protected fun loadData() {
        if (isOnline(requireContext())) {
            showLoader()
            checkListTestViewModel.getCheckListTest(TipoCheckList, idChecklist, IdCompany)
        } else Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
            .setAction("Reintentar") { loadData() }.show()
    }

    protected fun showLoader() {
        loader = MaterialDialog.Builder(requireContext())
            .title("Procesando")
            .content("Espere, por favor.")
            .cancelable(false)
            .autoDismiss(false)
            .progress(true, 0)
            .show()
    }
    private fun updateButtonVisibility() {
        val isDivision = checkListTestm?.isValidDivisionId(checkListTestm?.divisionId) == true

        if (TipoCheckList == "TDV" && isDivision && checkListTestm?.grupoList?.any { it.groupType == "CRITICO" && it.preguntas.any { pregunta -> pregunta.valorChecked == "NO" } } == true) {
            binding.txtAlert.visibility=View.VISIBLE
            binding.btnEnviarResultados.visibility = View.GONE
        } else {
            binding.txtAlert.visibility=View.GONE
            binding.btnEnviarResultados.visibility = View.VISIBLE
        }
    }
    override fun onClickButtonSI(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateRespuestaCheckListtest_Detalle(
                checkListTestm!!.idDb,
                item.groupId,
                checkListTestm!!.idTest,
                item.idTest_Detalle,
                true,
                "SI"
            )
            var i = 0
            while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
                if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                    var j = 0
                    while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                        if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                            checkListTestm!!.grupoList[i].preguntas[j].isChecked = true
                            checkListTestm!!.grupoList[i].preguntas[j].valorChecked = "SI"
                            checkListTestm!!.grupoList[i].preguntas[j].btnSIActive = true
                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
            updateButtonVisibility()
        }
    }

    private fun ShowCdlDialog(flag: Boolean, msg: String) {
        var message: String
        var auxMsg: String
        val phone =
            if (IdCompany.equals(Companies.GF.valor)) SharedUtils.getTelefonoCovidGF(requireContext()) else SharedUtils.getTelefonoCovidCDL(
                requireContext()
            )
        if (!flag) {
            if (IdCompany == Companies.BR.valor || IdCompany == Companies.GF.valor) {
                message =
                    "Estimado. Informe inmediatamente a su supervisor. Tiene observaciones en la encuesta Covid," +
                            "\nPor lo tanto queda bloqueado su ingreso a faena.\n" +
                            "Comuníquese con RRHH de su empresa"
            } else {
                if (IdCompany == Companies.MC.valor) {
                    message =
                        "El CheckList se guardo satisfactoriamente.\n" + "Se enviará la constancia al Correo Electronico con el cual se registro en Web control APP.  Gracias"
                } else if(IdCompany == Companies.CDL.valor){
                    message =
                        "ESTIMADO. INFORME INMEDIATAMENTE A SU SUPERVISOR POR QUE USTED TIENE OBSERVACIONES EN LA ENCUESTA COVID. " +
                                "\nSI ESTA POSITIVO POR COVID QUEDARA BLOQUEADO POR 5 DIAS.\n" +
                                "SI FUE ALERTA COVID DEBE REALIZARSE TEST PCR O ANTIGENO PARA RETORNAR "
                } else {
                    message =
                        "Estimado. Informe inmediatamente a su supervisor. Tiene observaciones en la encuesta Covid," +
                                "\nPor lo tanto queda bloqueado su ingreso a faena.\n" +
                                "Comuníquese al siguiente numero: " + phone
                }
            }
        } else if (msg.isNullOrEmpty())
            message = "Ud. No presenta observaciones, Gracias por rendir la encuesta"
        else {
            if (IdCompany == Companies.MC.valor) {
                message =
                    "El CheckList se guardo satisfactoriamente.\n" + "Se enviará la constancia al Correo Electronico con el cual se registro en Web control APP.  Gracias"
            } else {
                auxMsg = msg.replace(",", "\n")
                message =
                    "Estimado, Ud. No presenta observaciones en su encuesta, sin embargo no será autorizado hasta" +
                            " regularizar las siguientes observaciones: \n" + auxMsg + "\nGracias por rendir la encuesta. "
            }
        }
        MaterialDialog.Builder(requireContext())
            .title("Advertencia")
            .content(message)
            .positiveText("ACEPTAR")
            .negativeText("CANCELAR")
            .autoDismiss(true)
            .onPositive { dialog, which ->

            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun onClickButtonNO(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateRespuestaCheckListtest_Detalle(
                checkListTestm!!.idDb,
                item.groupId,
                checkListTestm!!.idTest,
                item.idTest_Detalle,
                false,
                "NO"
            )
            val checklistGroup = App.db.checklistGroupsDao().getOnebyUniqueId(
                checkListTestm!!.idDb,
                checkListTestm!!.tipoTest,
                checkListTestm!!.idTest,
                item.groupId
            )
            if (TipoCheckList != "TFS" && TipoCheckList != "ENC" && TipoCheckList != "TMZ" && TipoCheckList != "COV" && TipoCheckList != "EQV" && TipoCheckList != "DDS") {
                if (checklistGroup != null) {
                    if (checklistGroup.groupMessage != null) {
                        if (checklistGroup.groupMessage!!.isNotEmpty()) {
                            MaterialDialog.Builder(requireContext())
                                .title("ALERTA")
                                .content(checklistGroup.groupMessage!!)
                                .autoDismiss(false)
                                .cancelable(false)
                                .positiveText("ACEPTAR")
                                .onPositive { dialog: MaterialDialog, which: DialogAction? -> dialog.dismiss() }
                                .show()
                        }
                    }
                }
            }
            var i = 0
            while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
                if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                    var j = 0
                    while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                        if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                            checkListTestm!!.grupoList[i].preguntas[j].isChecked = false
                            checkListTestm!!.grupoList[i].preguntas[j].valorChecked = "NO"
                            checkListTestm!!.grupoList[i].preguntas[j].btnSIActive = true
                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
            updateButtonVisibility()
        }
    }

    override fun onClickButtonNOApply(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateRespuestaCheckListtest_Detalle(
                checkListTestm!!.idDb,
                item.groupId,
                checkListTestm!!.idTest,
                item.idTest_Detalle,
                false,
                "NA"
            )
            var i = 0
            while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
                if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                    var j = 0
                    while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                        if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                            checkListTestm!!.grupoList[i].preguntas[j].isChecked = false
                            checkListTestm!!.grupoList[i].preguntas[j].valorChecked = "NA"
                            checkListTestm!!.grupoList[i].preguntas[j].btnSIActive = false
                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
            updateButtonVisibility()
        }
    }

    override fun onTextChanged(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateValorSeleccionadoCheckListDetalle(
                checkListTestm!!.idDb,
                item.groupId,
                checkListTestm!!.idTest,
                item.idTest_Detalle,
                item.respuestaSeleccionada
            )
            var i = 0
            while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
                if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                    var j = 0
                    while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                        if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                            checkListTestm!!.grupoList[i].preguntas[j].respuestaSeleccionada =
                                item.respuestaSeleccionada

                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
        }
    }

    //se actualiza el valor de respuesta seleccionada para el envio de resultados
    override fun onCheck(value: String, item: CheckListTest_Detalle) {
        var isUpgrade = false
        var i = 0
        while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
            if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                var j = 0
                while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                    if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                        checkListTestm!!.grupoList[i].preguntas[j].respuestaSeleccionada = value
                        isUpgrade = true
                    }
                    j++
                }
            }
            i++
        }
    }

    override fun onBackPressed(): Boolean {
        if (TipoCheckList != "TFS" && TipoCheckList != "ENC" && TipoCheckList != "TMZ" && TipoCheckList != "COV" && TipoCheckList != "EQV" && TipoCheckList != "DDS") {
            NomCheckList = "Vehículos"
        }
        MaterialDialog.Builder(requireContext())
            .title("Confirmación")
            .content("¿Está seguro que desea regresar? Checklist $NomCheckList no guardado")
            .positiveText("SI")
            .negativeText("NO")
            .autoDismiss(false)
            .cancelable(false)
            .onPositive { dialog, which ->
                dialog.dismiss()
                if (TipoCheckList == "TMZ") {
                    SharedUtils.setTmzNow(context, "19900101")
                }
                val checkListTest = App.db.checkListDao().selectCheckListById(
                    App.db.checkListDao()
                        .getLastInsertedCheckListTestId(requireArguments().getString(ARG_PARAM1))
                )
                App.db.checkListDao().deleteCheckListTestDetalle(checkListTest!!.idDb)
                App.db.checkListDao().deleteCheckListTest(checkListTest)
                findNavController().navigate(
                    R.id.historicoCheckListFragment, bundleOf(
                        HistoricoCheckListFragment.CHECKLIST_TYPE to TipoCheckList,
                        HistoricoCheckListFragment.CHECKLIST_NAME to NomCheckList,
                        HistoricoCheckListFragment.CHECKLIST_ID to idChecklist,
                        HistoricoCheckListFragment.COMPANY_ID to IdCompany
                    )
                )
            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
        return false
    }

    private val isBlocked: Boolean
        get() {
            loader!!.show()
            for (i in checkListTestm!!.grupoList.indices) {
                for (j in checkListTestm!!.grupoList[i].preguntas.indices) {
                    if (checkListTestm!!.grupoList[i].preguntas[j].isChecked) {
                        return true
                    }
                }
            }
            return false
        }

    companion object {
        private val TAG = CheckListTestFragment::class.java.simpleName
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"

        @JvmStatic
        fun newInstance(
            param1: String?,
            param2: String?,
            param3: String?,
            param4: String? = null
        ): CheckListTestFragment {
            val fragment = CheckListTestFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            args.putString(ARG_PARAM3, param3)
            args.putString(ARG_PARAM4, param4)
            fragment.arguments = args
            return fragment
        }
    }
}