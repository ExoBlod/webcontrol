package com.webcontrol.android.ui.preacceso

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.ChecklistGroups
import com.webcontrol.android.databinding.FragmentTestCheckListBinding
import com.webcontrol.android.ui.common.adapters.*
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getCompanyCheckList
import com.webcontrol.android.util.SharedUtils.getNiceDate
import com.webcontrol.android.util.SharedUtils.getOSTCheckList
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.vm.CheckListTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CheckListTestActivity : AppCompatActivity() , CheckListAdapterListener, OnCheckListener {
    private var checkListTestViewModel: CheckListTestViewModel? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val checkListTestAdapter: CheckListTestAdapter? = null
    private var checkListTestm: CheckListTest? = null
    private lateinit var binding: FragmentTestCheckListBinding
    private lateinit var loader: MaterialDialog
    private var TipoCheckList: String? = null
    private var NomCheckList: String? = null
    private var idChecklist: String? = null
    private var IdCompany: String? = ""
    private var questionsList: MutableList<CheckListTest_Detalle>? = null
    private var pass = true
    private var aproboTPA = false
    private var esPasajero = false
    private var rutPasajero:String? = ""
    private var lastId:String? = ""
    private var isParticularPreAccess = false
    lateinit private var parentLayout : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoCheckList = intent.getStringExtra(ARG_PARAM1)
        NomCheckList = intent.getStringExtra(ARG_PARAM2)
        idChecklist = intent.getStringExtra(ARG_PARAM3)
        rutPasajero = intent.getStringExtra(ARG_PARAM4)
        lastId = intent.getStringExtra(ARG_PARAM5)
        isParticularPreAccess = intent.getBooleanExtra(ARG_PARAM6, false)
        binding = FragmentTestCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.fragment_test_check_list)
        parentLayout = findViewById<View>(android.R.id.content)
        checkListTestViewModel = ViewModelProvider(this).get(CheckListTestViewModel::class.java)
        binding.btnEnviarResultados.setOnClickListener {
            EnviarResultados()
        }
        binding.lblFecha.text = getNiceDate(SharedUtils.wCDate)
        binding.lblHora.text = SharedUtils.time
        IdCompany = SharedUtils.getIdCompany(this)
        initializeRecycler()
        loadData()
        
        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> = ArrayList()
        checkListTestViewModel!!.getCheckListResult().observe(this, Observer { checkListTest ->
            if (checkListTest != null) {
                val idcheckListLast = App.db.checkListDao().getLastInsertedCheckListTestId(TipoCheckList)
                checkListTestm = App.db.checkListDao().selectCheckListById(idcheckListLast)
                checkListTestm!!.grupoList = checkListTest.grupoList
                binding.lblNomTest.text = checkListTestm!!.titulo
                var numPreguntas = 0
                questionsList = ArrayList<CheckListTest_Detalle>()
                for (i in checkListTestm!!.grupoList.indices) {
                    val preguntas = checkListTestm!!.grupoList[i].preguntas.sortedBy { it.idTest_Detalle }
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
                        questionsList!!.add(item)
                    }
                    sections.add(SimpleSectionedRecyclerViewAdapter.Section(numPreguntas,
                        checkListTestm!!.grupoList[i].groupName!!
                    ))
                    numPreguntas += checkListTestm!!.grupoList[i].preguntas.size
                }
                binding.lblNumPreguntas.text = "$numPreguntas Preguntas"
                //checkListTestAdapter = new CheckListTestAdapter(getthis(), questionsList, CheckListTestFragment.this, false, TipoCheckList, this);
                val typeFactory: TypeFactory = TypeFactoryImpl()
                val adapter = ChecklistAdapter(this, questionsList!!, typeFactory, false, TipoCheckList!!, this, this)
                val dummy = arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
                val mSectionedAdapter = SimpleSectionedRecyclerViewAdapter(this, R.layout.section, R.id.section_text, adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                mSectionedAdapter.setSections(sections.toTypedArray())
                binding.rcvTestList.adapter = mSectionedAdapter
            }
            loader.dismiss()
        })

        checkListTestViewModel!!.getCheckListError().observe(this, Observer { result ->
            loader.dismiss()
            Log.e(TAG, result.toString())
            Snackbar.make(parentLayout, "Error de red", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar") { v: View? -> loadData() }.show()
        })


        checkListTestViewModel!!.getCheckListEnvioResult().observe(this, Observer { result ->
            if (result && (TipoCheckList == "TPA" || TipoCheckList == "VTP")) {
                if (aproboTPA && esPasajero) {
                    val intent = intent
                    intent.putExtra("Ok", "OK")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if(aproboTPA && !esPasajero) {
                    val intent = Intent(this@CheckListTestActivity, PasajeroActivity::class.java)
                    val preaccessId = if (lastId.isNullOrEmpty())
                        0
                    else
                        lastId!!.toInt()
                    intent.putExtra("PREACCESS_ID", preaccessId)
                    intent.putExtra("CLIENT", Companies.ANGLO.valor)
                    intent.putExtra("IS_PARTICULAR", isParticularPreAccess)
                    startActivity(intent)
                    finish()
                } else {
                    MaterialDialog.Builder(this)
                            .title("Notice")
                            .content("Usted no esta autorizado para ingresar a faena.")
                            .cancelable(false)
                            .positiveText("Aceptar")
                            .onPositive { _, _ ->
                                finish()
                            }
                            .show()
                }
            } else {
                Toast.makeText(this, "No se pudo completar la operación intentelo nuevamente", Toast.LENGTH_LONG).show()
            }
            loader.dismiss()
            ProgressLoadingJIGB.finishLoadingJIGB(this)
        })

        checkListTestViewModel!!.getCheckListEnvioError().observe(this, Observer { result ->
            MaterialDialog.Builder(this)
                    .title("Error")
                    .content("No se pudo completar la operación intentelo nuevamente")
                    .cancelable(false)
                    .positiveText("Aceptar")
                    .show()
            loader.dismiss()
            ProgressLoadingJIGB.finishLoadingJIGB(this)
        })

        checkListTestViewModel!!.blockPassResult.observe(this, Observer { result: Boolean ->
            pass = if (result || TipoCheckList == "COV") {
                true
                //Toast.makeText(getthis(), "Su pase fue bloqueado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No se pudo completar la operación intentelo nuevamente", Toast.LENGTH_LONG).show()
                false
            }
            loader.dismiss()
        })

        checkListTestViewModel!!.getBlockPassError().observe(this, Observer { s ->
            loader.dismiss()
            Toast.makeText(this, "No se pudo bloquear su pase, intentelo nuevamente", Toast.LENGTH_LONG).show()
            pass = false
        })

        if(!rutPasajero.isNullOrEmpty()){
            esPasajero = true
        }
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
        if (revisarRespondidos() && TipoCheckList == "TMZ") {
            MaterialDialog.Builder(this)
                    .title("Alerta")
                    .content("Responda todas las preguntas")
                    .positiveText("Aceptar")
                    .autoDismiss(true)
                    .show()

        } else if (TipoCheckList == "TMZ") {
            MaterialDialog.Builder(this)
                    .title("Resumen Respuestas")

                    .content("Marque SI confirmar y NO para modificar:" + '\n' +
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
            if (!TipoCheckList.equals("COV"))
                alertasEnviarResultados()
        }

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
        var mensajeFinal = "Puede ingresar a Faena Los Bronces/Las Tórtolas\n\nRECUERDE: \nSIN PERJUICIO QUE LAS MANTENCIONES Y/O REVISIONES PERIÓDICAS SEAN EFECTUADAS POR EL CONDUCTOR U OTRO SEGÚN LA FRECUENCIA DEFINIDA, ES OBLIGACIÓN DEL CONDUCTOR VERIFICAR AL INICIO DE CADA VIAJE EL BUEN FUNCIONAMIENTO DEL VEHÍCULO, DETENIENDO DE MANERA INMEDIATA SI DETECTARA ALGUNA CAUSA DE “Detención inmediata” acorde a esta lista de verificación"
        if (isOnline(this)) {
            if (TipoCheckList == "TPA" || TipoCheckList == "VTP") {
                mensajeFinal = "¿Enviar encuesta?"
            }
            MaterialDialog.Builder(this)
                    .title("Warning")
                    .content(mensajeFinal)
                    .positiveText("ACEPTAR")
                    .negativeText("CANCELAR")
                    .autoDismiss(true)
                    .onPositive { dialog, which ->
                        if (checkListTestm != null) {
                            if (TipoCheckList == "TMZ") {
                                val date = SharedUtils.wCDate
                                SharedUtils.setTmzNow(this, date)
                            }
                            if (checkListTestm!!.grupoList.size > 0) {
                                checkListTestm!!.fechaSubmit = SharedUtils.wCDate
                                checkListTestm!!.horaSubmit = SharedUtils.time
                                checkListTestm!!.ost = getOSTCheckList(this)
                                checkListTestm!!.companyId = getCompanyCheckList(this)
                                if (TipoCheckList == "TPA" || TipoCheckList == "VTP") {
                                    val preguntas = checkListTestm!!.grupoList[0].preguntas
                                    for (pregunta in preguntas) {
                                        if (pregunta.isChecked) {
                                            aproboTPA = true
                                        } else {
                                            aproboTPA = false
                                            break
                                        }
                                    }
                                    if (esPasajero) {
                                        checkListTestm!!.workerId = rutPasajero.toString()
                                    }
                                }
                                if (isOnline(this)) {
                                    if (pass) {
                                        ProgressLoadingJIGB.startLoadingJIGB(this, R.raw.loaddinglottie, "Cargando...", 0, 500, 200)
                                        if (TipoCheckList != "COV") {
                                            if(TipoCheckList == "TPA")
                                                checkListTestm?.email = SharedUtils.get_email(this)
                                            checkListTestViewModel!!.sendCheckListTest(checkListTestm)
                                        }
                                        else
                                            checkListTestViewModel!!.sendCheckListTestCOV(checkListTestm, IdCompany)
                                    } else {
                                        Snackbar.make(parentLayout, "No se pudo enviar", Snackbar.LENGTH_LONG).setAction("Reintentar") { EnviarResultados() }.show()
                                    }
                                } else {
                                    Snackbar.make(parentLayout, "Error de red", Snackbar.LENGTH_LONG).setAction("Reintentar") { EnviarResultados() }.show()
                                }
                            } else Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_LONG).show()
                        } else Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_LONG).show()
                    }
                    .onNegative { dialog, which -> dialog.dismiss() }
                    .show()
        } else showToast(this, "No hay conexion a internet. Inténtelo mas tarde")
    }


    protected fun initializeRecycler() {
        val divider: ItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        linearLayoutManager = LinearLayoutManager(this)
        binding.rcvTestList.setHasFixedSize(true)
        binding.rcvTestList.layoutManager = linearLayoutManager
        binding.rcvTestList.addItemDecoration(divider)
    }

    protected fun loadData() {
        if (isOnline(this)) {
            showLoader()
            checkListTestViewModel!!.getCheckListTest(TipoCheckList, idChecklist, IdCompany)
        } else Snackbar.make(parentLayout, "Error de red", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar") { loadData() }.show()
    }

    protected fun showLoader() {
        loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
    }

    override fun onClickButtonSI(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateRespuestaCheckListtest_Detalle(checkListTestm!!.idDb, item.groupId, checkListTestm!!.idTest, item.idTest_Detalle, true)
            var i = 0
            while (i < checkListTestm!!.grupoList.size && !isUpgrade) {
                if (checkListTestm!!.grupoList[i].checklistGroupId == item.groupId) {
                    var j = 0
                    while (j < checkListTestm!!.grupoList[i].preguntas.size && !isUpgrade) {
                        if (item.idTest_Detalle == checkListTestm!!.grupoList[i].preguntas[j].idTest_Detalle) {
                            checkListTestm!!.grupoList[i].preguntas[j].isChecked = true
                            checkListTestm!!.grupoList[i].preguntas[j].btnSIActive =true

                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
        }
    }

    override fun onClickButtonNO(item: CheckListTest_Detalle) {
        var isUpgrade = false
        if (checkListTestm != null) {
            App.db.checkListDao().updateRespuestaCheckListtest_Detalle(checkListTestm!!.idDb, item.groupId, checkListTestm!!.idTest, item.idTest_Detalle, false)
            val checklistGroup = App.db.checklistGroupsDao().getOnebyUniqueId(checkListTestm!!.idDb, checkListTestm!!.tipoTest, checkListTestm!!.idTest, item.groupId)
            if (TipoCheckList != "TFS" && TipoCheckList != "ENC" && TipoCheckList != "TMZ" && TipoCheckList != "COV" && TipoCheckList != "EQV") {
                if (checklistGroup != null) {
                    if (checklistGroup.groupMessage != null) {
                        if (checklistGroup.groupMessage!!.isNotEmpty()) {
                            MaterialDialog.Builder(this)
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
                            checkListTestm!!.grupoList[i].preguntas[j].btnSIActive=true
                            isUpgrade = true
                        }
                        j++
                    }
                }
                i++
            }
        }
    }

    override fun onClickButtonNOApply(item: CheckListTest_Detalle) {}

    override fun onTextChanged(item: CheckListTest_Detalle) {}

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

    override fun onBackPressed() {
        MaterialDialog.Builder(this@CheckListTestActivity)
                .title("Confirmación")
                .content("¿Está seguro que desea regresar? Control de Preacceso no guardado")
                .positiveText("SI")
                .negativeText("NO")
                .autoDismiss(false)
                .cancelable(false)
                .onPositive { _, _ ->
                    finish()
                }
                .onNegative { dialog, which -> dialog.dismiss() }
                .show()
    }

    fun onBackPressed2() {
        if (TipoCheckList != "TFS" && TipoCheckList != "ENC" && TipoCheckList != "TMZ" && TipoCheckList != "COV" && TipoCheckList != "EQV") {
            NomCheckList = "Vehículos"
        }
        MaterialDialog.Builder(this)
                .title("Confirmación")
                .content("¿Está seguro que desea regresar? Checklist $NomCheckList no guardado")
                .positiveText("SI")
                .negativeText("NO")
                .autoDismiss(false)
                .cancelable(false)
                .onPositive { dialog, _ ->
                    dialog.dismiss()
                    if (TipoCheckList == "TMZ") {
                        SharedUtils.setTmzNow(this, "19900101")
                    }
                    val checkListTest = App.db.checkListDao().selectCheckListById(App.db.checkListDao().getLastInsertedCheckListTestId(intent.getStringExtra(ARG_PARAM1)))
                    App.db.checkListDao().deleteCheckListTestDetalle(checkListTest!!.idDb)
                    App.db.checkListDao().deleteCheckListTest(checkListTest)
                    /*findNavController().navigate(R.id.historicoCheckListFragment, bundleOf("TipoCheckList" to TipoCheckList,
                        "NomCheckList" to NomCheckList,"idChecklist" to idChecklist, "IdCompany" to IdCompany))*/
                }
                .onNegative { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private val isBlocked: Boolean
        private get() {
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
        private val TAG = CheckListTestActivity::class.java.simpleName
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"
        private const val ARG_PARAM5 = "param5"
        private const val ARG_PARAM6 = "param6"
        @JvmStatic
        fun newInstance(context: Context, param1: String?, param2: String?, param3: String?, param4: String? = null, param5: String? = null, param6: Boolean): Intent {

            val intent = Intent(context, CheckListTestActivity::class.java)
            intent.putExtra(ARG_PARAM1, param1)
            intent.putExtra(ARG_PARAM1, param1)
            intent.putExtra(ARG_PARAM2, param2)
            intent.putExtra(ARG_PARAM3, param3)
            intent.putExtra(ARG_PARAM4, param4)
            intent.putExtra(ARG_PARAM5, param5)
            intent.putExtra(ARG_PARAM6, param6)
            return intent
        }
    }
}