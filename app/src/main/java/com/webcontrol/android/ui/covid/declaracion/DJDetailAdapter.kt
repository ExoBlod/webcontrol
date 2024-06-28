package com.webcontrol.android.ui.covid.declaracion

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.webcontrol.android.R
import com.webcontrol.android.data.OnDJItemClickListener
import com.webcontrol.android.data.model.*
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.row_historico_check_list.view.*
import kotlinx.android.synthetic.main.row_historico_check_list.view.lblTestTitle
import kotlinx.android.synthetic.main.row_question_date.view.lbl_answer as date_lbl_answer
import kotlinx.android.synthetic.main.row_question_om_os.view.rcv_answers
import kotlinx.android.synthetic.main.row_question_trav.view.*
import kotlinx.android.synthetic.main.row_question_trpe.view.*
import kotlinx.android.synthetic.main.row_question_txt.view.*
import java.lang.Exception
import kotlinx.android.synthetic.main.row_question_txt.view.lbl_answer as txt_lbl_answer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DJDetailAdapter(private val context: Context, testListResponse: ArrayList<DJRespuesta>)
    : TestRecyclerViewAdapter<DJPregunta>() {

    private var testList: ArrayList<DJRespuesta> = testListResponse
    private val viewPool = RecyclerView.RecycledViewPool()

    companion object {
        private const val YESNO = 0
        private const val TRPE = 1
        private const val OM = 2
        private const val TRAV = 3
        private const val TXT = 4
        private const val DATE = 5
        private const val TAG = "DJDetailAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder = when (viewType) {
        YESNO -> YesNoViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_historico_check_list,
                parent,
                false
        ))
        TRPE -> TRPEViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_trpe,
                parent,
                false
        ))
        OM -> OMViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_om_os,
                parent,
                false
        ))
        TRAV -> TRAVViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_trav,
                parent,
                false
        ))
        TXT -> TXTViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_txt,
                parent,
                false
        ))
        DATE -> DATEViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_date,
                parent,
                false
        ))
        else -> throw IllegalArgumentException("Tipo de vista inválido: $viewType")
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int) = when (holder.itemViewType) {
        YESNO -> onBindYesNo(holder, position)
        TRPE -> onBindTRPE(holder, position)
        OM ->  onBindOM(holder, position)
        TRAV -> onBindTRAV(holder, position)
        TXT -> onBindTXT(holder, position)
        DATE -> onBindDATE(holder, position)
        else -> throw IllegalArgumentException("BindViewHolder inválido: #$position")
    }

    private fun onBindDATE(holder: RecyclerView.ViewHolder, position: Int) {
        val dateHolder = holder as DATEViewHolder
        dateHolder.setUpView(cuestionario = getItem(position))
    }

    private fun onBindTXT(holder: RecyclerView.ViewHolder, position: Int) {
        val txtHolder = holder as TXTViewHolder
        txtHolder.setUpView(cuestionario = getItem(position))
    }

    private fun onBindYesNo(holder: RecyclerView.ViewHolder, position: Int) {
        val yesNoHolder = holder as YesNoViewHolder
        yesNoHolder.setUpView(cuestionario = getItem(position))
    }

    private fun onBindTRPE(holder: RecyclerView.ViewHolder, position: Int) {
        val trpeHolder = holder as TRPEViewHolder
        val cuestionario = getItem(position)
        val childLayoutManager = LinearLayoutManager(holder.itemView.trpe_rcv_answers.context)
        val itemAdapter = DJTRPEAdapter()
        trpeHolder.setUpView(cuestionario, itemAdapter)

        cuestionario?.let {
            getResponseItem(cuestionario.id)?.let {found ->
                itemAdapter.clear()
                found.lugares?.let { lugares -> itemAdapter.addItems(lugares) }

            }
            trpeHolder.itemView.trpe_rcv_answers.apply {
                layoutManager = childLayoutManager
                adapter = itemAdapter
                setRecycledViewPool(viewPool)
            }
        }
    }

    private fun onBindOM(holder: RecyclerView.ViewHolder, position: Int) {
        val omHolder = holder as OMViewHolder
        val pregunta = getItem(position)
        val childLayoutManager = LinearLayoutManager(holder.itemView.rcv_answers.context)
        omHolder.setUpView(pregunta)
        pregunta?.let {
            it.alternativas?.let { alternativas ->

                var stringList = alternativas.split(",")
                        .map { str -> str.trim() } as ArrayList<String>
                var responseStringList = getResponseItem(it.id)?.respuesta?.split(",")
                        ?.map { str -> str.trim() } as ArrayList<String>? ?: ArrayList()

                val sourceList = ArrayList<Pair<String, Boolean>>()
                stringList.forEach {
                    val found = responseStringList.contains(it)
                    sourceList.add(Pair(it,found))
                }

                val itemAdapter = DJOMAdapter()
                itemAdapter.addItems(sourceList)
                itemAdapter.setOnDJItemClickListener(onItemClickListener = object : OnDJItemClickListener {
                    override fun onItemClick(position: Int, view: View?, data: String) {
                        stringList = alternativas.split(",")
                                .map { str -> str.trim() } as ArrayList<String>
                        responseStringList = getResponseItem(it.id)?.respuesta?.split(",")
                                ?.map { str -> str.trim() } as ArrayList<String>? ?: ArrayList()
                        if (it.tipo == "OMU") {
                            responseStringList.clear()
                            responseStringList.add(data)
                        } else {
                            responseStringList.contains(data).let { result ->
                                if (result)
                                    responseStringList.remove(data)
                                else
                                    responseStringList.add(data)
                            }
                        }

                        if (responseStringList.contains("NO"))
                            responseStringList.remove("NO")
                        else if (responseStringList.isEmpty())
                            responseStringList.add("NO")

                        sourceList.clear()
                        stringList.forEach {
                            val found = responseStringList.contains(it)
                            sourceList.add(Pair(it,found))
                        }
                        itemAdapter.clear()
                        itemAdapter.addItems(sourceList)
                        itemAdapter.notifyDataSetChanged()

                        processResponse(testList, it, true, responseStringList.joinToString())
                    }
                })

                omHolder.itemView.rcv_answers.apply {
                    layoutManager = childLayoutManager
                    adapter = itemAdapter
                    setRecycledViewPool(viewPool)
                }
            }
        }
    }

    private fun onBindTRAV(holder: RecyclerView.ViewHolder, position: Int) {
        val travHolder = holder as TRAVViewHolder
        travHolder.setUpView(cuestionario = getItem(position))
    }

    private fun <T> getObjectFromString(source: String): T {
        val gson = Gson()
        val arrayType = object : TypeToken<T>() {}.type
        return gson.fromJson(source, arrayType)
    }

    private fun processResponse(
            resultList: java.util.ArrayList<DJRespuesta>,
            cuestionario: DJPregunta,
            modificar: Boolean = false,
            respuesta: String? = null) {
        resultList.find { it.idHito == cuestionario.id }.let { found ->
            if (found != null) {
                if (modificar){
                    found.respuesta = respuesta ?: ""
                } else
                    testList.remove(found)
            }
            else {
                val response = DJRespuesta (
                        rut = SharedUtils.getUsuarioId(context),
                        idHito = cuestionario.id,
                        fecha = SharedUtils.wCDate,
                        respuesta = respuesta ?: "",
                        tipo = cuestionario.tipo ?: ""
                )
                resultList.add(response)
            }
        }
    }

    private fun processLugarResponse(
            resultList: java.util.ArrayList<DJRespuesta>,
            cuestionario: DJPregunta,
            listLugares: ArrayList<DJLugares>) {
        resultList.find { it.idHito == cuestionario.id }.let { found ->

            if (found != null) {
                found.lugares = listLugares
            }
            else {
                val response = DJRespuesta (
                        rut = SharedUtils.getUsuarioId(context),
                        idHito = cuestionario.id,
                        fecha = SharedUtils.wCDate,
                        respuesta = "",
                        tipo = cuestionario.tipo ?: "",
                        lugares = listLugares
                )
                resultList.add(response)
            }
        }
    }

    private fun getResponseItem(index: Int): DJRespuesta? {
        return testList.find { it.idHito == index }
    }

    inner class DATEViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val testTitleTextView: TextView = view.lblTestTitle
        private val testAnswer: EditText = view.date_lbl_answer
        private val calendar = Calendar.getInstance()

        fun setUpView(cuestionario: DJPregunta?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion

                testAnswer.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        try {
                            val result = SharedUtils.getWCDateformat(s.toString())
                            processResponse(testList, cuestionario, true, result)
                        } catch (ex: Exception) {
                            SharedUtils.showToast(context, "Fecha inválida")
                        }
                    }

                })

                testAnswer.setOnClickListener {
                    DatePickerDialog(context, setOnDatePickListener(testAnswer, calendar),
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
    }

    inner class TXTViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val testTitleTextView: TextView = view.lblTestTitle
        private val testAnswer: EditText = view.txt_lbl_answer

        fun setUpView(cuestionario: DJPregunta?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion

                testAnswer.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        processResponse(testList, cuestionario,true, s.toString())
                    }

                })
            }
        }
    }

    inner class YesNoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val testTitleTextView: TextView = view.lblTestTitle
        private val btnRight: Button = view.sekizbit_switch_btn_right
        private val btnLeft: Button = view.sekizbit_switch_btn_left

        fun setUpView(cuestionario: DJPregunta?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion
                btnRight.text = "SI"
                btnLeft.text = "NO"
                getResponseItem(it.id)?.let { response ->
                    when (response.respuesta) {
                        "SI" -> {
                            btnRight.isSelected = true
                            btnLeft.isSelected = false
                        }
                        "NO" -> {
                            btnRight.isSelected = false
                            btnLeft.isSelected = true
                        }
                    }
                }
            }
            btnRight.setOnClickListener {
                cuestionario?.let { exists ->
                    processResponse(testList, exists, modificar = true, respuesta = "SI")
                    it.isSelected = true
                    btnLeft.isSelected = false
                }
            }
            btnLeft.setOnClickListener {
                cuestionario?.let { exists ->
                    processResponse(testList, exists, modificar = true, respuesta = "NO")
                    it.isSelected = true
                    btnRight.isSelected = false
                }
            }
        }
    }

    inner class TRAVViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val testTitleSwitch: Switch = view.switchTestTitle
        private val cardContainer: CardView = view.card_container
        private val dateIn: EditText = view.trav_lbl_date_in
        private val dateOut: EditText = view.trav_lbl_date_out
        private val pais: EditText = view.trav_txt_pais
        private val calendar = Calendar.getInstance()
        private var lugar = DJLugares()

        fun setUpView(cuestionario: DJPregunta?) {
            cuestionario?.let {
                // state
                testTitleSwitch.text = it.descripcion
                dateIn.isFocusable = false
                dateOut.isFocusable = false
                cardContainer.visibility = if (getResponseItem(it.id) != null) View.VISIBLE else View.GONE
                if (!testTitleSwitch.isChecked)
                    processResponse(testList, it, true, "NO")
                // actions
                testTitleSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cardContainer.visibility = View.VISIBLE
                        processResponse(testList, it, true, "SI")
                    } else {
                        cardContainer.visibility = View.GONE
                        processResponse(testList, it, true, "NO")
                    }

                }
                pais.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) { }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        lugar.descripcion = s.toString()
                        getResponseItem(it.id)?.let { responseItem ->
                            responseItem.lugares.let { lugares ->
                                if (lugares != null)
                                    lugares[0].descripcion = s.toString()
                                else
                                    responseItem.lugares = arrayListOf(lugar)
                            }
                        }
                    }
                })
                dateIn.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        lugar.fechaIn = SharedUtils.getWCDateformat(dateIn.text.toString())
                        getResponseItem(it.id)?.let { responseItem ->
                            responseItem.lugares.let { lugares ->
                                if (lugares != null)
                                    lugares[0].fechaIn = SharedUtils.getWCDateformat(dateIn.text.toString())
                                else
                                    responseItem.lugares = arrayListOf(lugar)
                            }
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                })
                dateIn.setOnClickListener {
                    DatePickerDialog(context, setOnDatePickListener(dateIn, calendar),
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                }
                dateOut.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        lugar.fechaOut = SharedUtils.getWCDateformat(dateOut.text.toString())
                        getResponseItem(it.id)?.let { responseItem ->
                            responseItem.lugares.let { lugares ->
                                if (lugares != null)
                                    lugares[0].fechaOut = SharedUtils.getWCDateformat(dateOut.text.toString())
                                else
                                    responseItem.lugares = arrayListOf(lugar)
                            }
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                })
                dateOut.setOnClickListener {
                    DatePickerDialog(context, setOnDatePickListener(dateOut, calendar),
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

            }
        }
    }

    private fun setOnDatePickListener(inputView: EditText, calendar: Calendar) : DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth  ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = "dd/MM/YYYY"
            val sdf = SimpleDateFormat(dateFormat, Locale.US)

            inputView.setText(sdf.format(calendar.time))
        }
    }

    inner class TRPEViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val testTitleTextView: Switch = view.trpe_switch_test_title
        private val cardContainer: CardView = view.trpe_card_container
        private val btnAgregar: MaterialButton = view.trpe_btn_agregar
        private val btnCancelar: MaterialButton = view.trpe_btn_cancelar
        private val btnAceptar: MaterialButton = view.trpe_btn_aceptar
        private val trpeResultList: ArrayList<DJLugares> = ArrayList()
        private var trpeModel: DJLugares? = null
        private val lugar: EditText = view.trpe_lugar_txt
        private val fechaRetorno: EditText = view.trpe_date_txt
        private val trpeRCV: RecyclerView = view.trpe_rcv_answers
        private val calendar = Calendar.getInstance()

        fun setUpView(cuestionario: DJPregunta?, itemAdapter: DJTRPEAdapter) {
            cuestionario?.let {
                fechaRetorno.isFocusable = false
                trpeRCV.visibility = View.VISIBLE
                testTitleTextView.text = it.descripcion
                cardContainer.visibility = if (testTitleTextView.isChecked) View.VISIBLE else View.GONE
                if (!testTitleTextView.isChecked)
                    processResponse(testList, it, true, "NO")

                btnAgregar.setOnClickListener {
                    cardContainer.visibility = View.VISIBLE
                    btnAgregar.visibility = View.GONE

                    trpeModel = DJLugares()
                }
                btnAceptar.setOnClickListener { _ ->
                    if (!lugar.text.isNullOrBlank() && !fechaRetorno.text.isNullOrBlank()) {
                        cardContainer.visibility = View.GONE
                        btnAgregar.visibility = View.VISIBLE
                        trpeModel?.let {exists ->
                            exists.descripcion = lugar.text.toString()
                            exists.fechaIn = SharedUtils.getWCDateformat(fechaRetorno.text.toString())
                            trpeResultList.add(exists)
                            getResponseItem(it.id).let {responseItem ->
                                if (responseItem != null)
                                    responseItem.lugares = trpeResultList
                                else
                                    processLugarResponse(testList, it, trpeResultList)
                            }

                            itemAdapter.clear()
                            itemAdapter.addItems(trpeResultList)
                        }
                        lugar.text.clear()
                        fechaRetorno.text.clear()
                    }

                }
                btnCancelar.setOnClickListener {
                    cardContainer.visibility = View.GONE
                    btnAgregar.visibility = View.VISIBLE

                    lugar.text.clear()
                    fechaRetorno.text.clear()
                }
                testTitleTextView.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        btnAgregar.visibility = View.VISIBLE
                        trpeRCV.visibility = View.VISIBLE
                        processResponse(testList, it, true, "SI")
                    } else {
                        cardContainer.visibility = View.GONE
                        btnAgregar.visibility = View.GONE
                        trpeRCV.visibility = View.GONE

                        lugar.text.clear()
                        fechaRetorno.text.clear()
                        processResponse(testList, it, true, "NO")
                    }

                }
                fechaRetorno.setOnClickListener {
                    DatePickerDialog(context, setOnDatePickListener(fechaRetorno, calendar),
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
    }

    inner class OMViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val testTitleTextView: TextView = view.lblTestTitle

        fun setUpView(cuestionario: DJPregunta?) {
            cuestionario?.let {pregunta ->
                testTitleTextView.text = pregunta.descripcion
                val respuesta = getResponseItem(pregunta.id)
                if (respuesta != null) {
                    if (respuesta.tipo == "OMU" && respuesta.respuesta.isEmpty())
                        processResponse(testList, pregunta, true, "NO")
                } else {
                    if (cuestionario.id == 1)
                        processResponse(testList, pregunta, true, "NO")
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val cuestionario = getItem(position)
        cuestionario?.let {
            return when (it.tipo) {
                "SN" -> YESNO
                "TRPE" -> TRPE
                "OM" -> OM
                "TRAV" -> TRAV
                "TXT" -> TXT
                "DATE" -> DATE
                else -> OM //throw IllegalArgumentException("Tipo de data inválido: #$position")
            }
        }
        return -1
    }
}