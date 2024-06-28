package com.webcontrol.android.ui.covid.antapaccay.cuestionarios

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.webcontrol.android.data.OnSubItemClickListener
import com.webcontrol.android.data.model.*
import com.webcontrol.android.ui.common.adapters.OMAdapter
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import com.webcontrol.android.ui.covid.cuestionarios.TRPEAdapter
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.row_historico_check_list.view.*
import kotlinx.android.synthetic.main.row_historico_check_list.view.lblTestTitle
import kotlinx.android.synthetic.main.row_question_date.view.lbl_answer as date_lbl_answer
import kotlinx.android.synthetic.main.row_question_om_os.view.rcv_answers
import kotlinx.android.synthetic.main.row_question_trav.view.*
import kotlinx.android.synthetic.main.row_question_travpc.view.*
import kotlinx.android.synthetic.main.row_question_trpe.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TestDetailAdapter(
        private val context: Context,
        testListResponse: ArrayList<CuestionarioResponse>,
        private val workerControlInicial: ControlInicial,
        private val testFormat: String)
    : TestRecyclerViewAdapter<Cuestionario>() {

    private var testList: ArrayList<CuestionarioResponse> = testListResponse
    private val viewPool = RecyclerView.RecycledViewPool()

    companion object {
        private const val YESNO = 0
        private const val TRPE = 1
        private const val OM = 2
        private const val TRAV = 3
        private const val TXT = 4
        private const val DATE = 5
        private const val TRAVPC = 6
        private const val TAG = "TestDetailAdapter"
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
        ), TXTEditTextListener())
        DATE -> DATEViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_date,
                parent,
                false
        ), DATEEditTextListener())
        TRAVPC -> TRAVPCViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.row_question_travpc,
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
        TRAVPC -> onBindTRAVPC(holder, position)
        else -> throw IllegalArgumentException("BindViewHolder inválido: #$position")
    }

    private fun onBindDATE(holder: RecyclerView.ViewHolder, position: Int) {
        val dateHolder = holder as DATEViewHolder
        val cuestionario =  getItem(position)
        dateHolder.dateListener.updateCuestionario(cuestionario)
        dateHolder.setUpView(cuestionario = cuestionario)
    }

    private fun onBindTXT(holder: RecyclerView.ViewHolder, position: Int) {
        val txtHolder = holder as TXTViewHolder
        val cuestionario =  getItem(position)
        txtHolder.editTextListener.updateCuestionario(cuestionario)
        txtHolder.setUpView(cuestionario = cuestionario)
    }

    private fun onBindYesNo(holder: RecyclerView.ViewHolder, position: Int) {
        val yesNoHolder = holder as YesNoViewHolder
        yesNoHolder.setUpView(cuestionario = getItem(position))
    }

    private fun onBindTRPE(holder: RecyclerView.ViewHolder, position: Int) {
        val trpeHolder = holder as TRPEViewHolder
        val cuestionario = getItem(position)
        val childLayoutManager = LinearLayoutManager(holder.itemView.trpe_rcv_answers.context)
        val itemAdapter = TRPEAdapter()
        trpeHolder.setUpView(cuestionario, itemAdapter)

        cuestionario?.let {
            it.alternativas?.let {alternativas ->
                testList.find {response ->
                    response.codCuestionario == cuestionario.codigo &&
                            response.codAlternativa == alternativas[0].codigo }?.let { found ->
                    Log.e(TAG, "Alternative found: $found")
                    itemAdapter.clear()
                    itemAdapter.addItems(getObjectFromString(found.dato!!))
                }
                trpeHolder.itemView.trpe_rcv_answers.apply {
                    layoutManager = childLayoutManager
                    adapter = itemAdapter
                    setRecycledViewPool(viewPool)
                }
            }
        }
    }

    private fun <T> getObjectFromString(source: String): T {
        val gson = Gson()
        val arrayType = object : TypeToken<T>() {}.type
        return gson.fromJson(source, arrayType)
    }

    private fun onBindOM(holder: RecyclerView.ViewHolder, position: Int) {
        val omHolder = holder as OMViewHolder
        val cuestionario = getItem(position)
        val childLayoutManager = LinearLayoutManager(holder.itemView.rcv_answers.context)
        val itemAdapter = OMAdapter(testList, context)
        omHolder.setUpView(cuestionario)
        cuestionario?.let {
            it.alternativas?.let { alternativas ->
                itemAdapter.setOnSubItemClickListener(onItemClickListener = object : OnSubItemClickListener {
                    override fun onOMItemClick(position: Int, view: View?, alternativa: Alternativa) {
                        processResponse(testList, alternativa)
                    }

                    override fun onOMTextChanged(position: Int, alternativa: Alternativa, text: String) {
                        processResponse(testList, alternativa, true, text)
                    }
                })
                itemAdapter.addItems(alternativas)
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

    private fun onBindTRAVPC(holder: RecyclerView.ViewHolder, position: Int) {
        val travPcHolder = holder as TRAVPCViewHolder
        travPcHolder.setUpView(cuestionario = getItem(position))
    }

    private fun processResponse(
            testList: java.util.ArrayList<CuestionarioResponse>,
            alternativa: Alternativa,
            modificar: Boolean = false,
            respuesta: String? = null) {
        testList.find {
            it.codCuestionario == alternativa.codCuestionario &&
            it.codAlternativa == alternativa.codigo }.let { found ->
            if (found != null) {
                if (modificar){
                    if (alternativa.comenta)
                        found.comentario = respuesta
                    else
                        found.dato = respuesta
                } else
                    testList.remove(found)
            }
            else {
                val cuestionarioResponse = CuestionarioResponse(
                    codControlInicial = workerControlInicial.id,
                    rut = SharedUtils.getUsuarioId(context),
                    codCuestionario = alternativa.codCuestionario,
                    codAlternativa = alternativa.codigo,
                    fecha = SharedUtils.wCDate,
                    codFormato = testFormat
                )
                if (respuesta != null) {
                   if (alternativa.comenta)
                       cuestionarioResponse.comentario = respuesta
                    else
                       cuestionarioResponse.dato = respuesta
                }
                testList.add(cuestionarioResponse)
            }
        }
    }

    private fun getResponseItem(index: Int): CuestionarioResponse? {
        return testList.find { it.codCuestionario == index }
    }

    inner class TRAVPCViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val testTitle: TextView = view.travpc_lblTestTitle
        private val testPais: EditText = view.travpc_pais_txt
        private val testCiudad: EditText = view.travpc_ciudad_txt

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                testTitle.text = it.descripcion

                testPais.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        it.alternativas?.get(0)?.let { alternativa ->
                            processResponse(testList, alternativa, true, s.toString())
                        }
                    }

                })

                testCiudad.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        it.alternativas?.get(1)?.let { alternativa ->
                            processResponse(testList, alternativa, true, s.toString())
                        }
                    }

                })
            }
        }
    }

    inner class DATEViewHolder(view: View, listener: DATEEditTextListener): RecyclerView.ViewHolder(view) {
        private val testTitleTextView: TextView = view.lblTestTitle
        private val testAnswer: EditText = view.date_lbl_answer
        private val calendar = Calendar.getInstance()
        val dateListener: DATEEditTextListener = listener

        init {
            testAnswer.addTextChangedListener(dateListener)
        }

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion
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

    inner class TXTViewHolder(view: View, listener: TXTEditTextListener): RecyclerView.ViewHolder(view) {
        private val testTitleTextView: TextView = view.findViewById(R.id.lblTestTitle)
        private val testAnswer: EditText = view.findViewById(R.id.lbl_answer)
        val editTextListener: TXTEditTextListener = listener

        init {
            testAnswer.addTextChangedListener(editTextListener)
        }

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion
                testAnswer.setText("")
                getResponseItem(cuestionario.codigo)?.dato?.let {
                    testAnswer.setText(it)
                }
            }
        }
    }

    inner class YesNoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val testTitleTextView: TextView = view.lblTestTitle
        private val btnRight: Button = view.sekizbit_switch_btn_right
        private val btnLeft: Button = view.sekizbit_switch_btn_left

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion
                btnRight.isSelected = false
                btnLeft.isSelected = false
                getResponseItem(it.codigo)?.let { response ->
                    it.alternativas?.find {
                        alternativa ->  response.codAlternativa == alternativa.codigo
                    }?.let { alternativa ->
                        when (alternativa.descripcion) {
                            "A. SI" -> {
                                btnRight.isSelected = true
                                btnLeft.isSelected = false
                            }
                            "B. NO" -> {
                                btnRight.isSelected = false
                                btnLeft.isSelected = true
                            }
                        }
                    }

                }
            }
            btnRight.setOnClickListener {
                cuestionario?.alternativas?.get(0)?.let {alternativa ->
                    itemClickListener?.onYesNOItemClick(adapterPosition, it, alternativa)
                    it.isSelected = true
                    btnLeft.isSelected = false
                }
            }
            btnLeft.setOnClickListener {
                cuestionario?.alternativas?.get(1)?.let {alternativa ->
                    itemClickListener?.onYesNOItemClick(adapterPosition, it, alternativa)
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
        private val travModel: TRAV = TRAV()

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                // state
                testTitleSwitch.text = it.descripcion
                dateIn.isFocusable = false
                dateOut.isFocusable = false
                cardContainer.visibility = if (travModel.checked) View.VISIBLE else View.GONE
                // actions
                testTitleSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cardContainer.visibility = View.VISIBLE
                    } else {
                        cardContainer.visibility = View.GONE
                    }
                    travModel.checked = isChecked

                    it.alternativas?.get(0)?.let {alternativa ->
                        processResponse(testList, alternativa, false, travModel.toString())
                    }
                }
                pais.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) { }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        travModel.pais = s.toString()
                        it.alternativas?.get(0)?.let {alternativa ->
                            processResponse(testList, alternativa, true, travModel.toString())
                        }
                    }

                })
                dateIn.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        travModel.fechaIn = SharedUtils.getWCDateformat(dateIn.text.toString())
                        it.alternativas?.get(0)?.let {alternativa ->
                            processResponse(testList, alternativa, true, travModel.toString())
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
                        travModel.fechaOut = SharedUtils.getWCDateformat(dateOut.text.toString())
                        it.alternativas?.get(0)?.let {alternativa ->
                            processResponse(testList, alternativa, true, travModel.toString())
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
        private val trpeResultList: ArrayList<TRPE> = ArrayList()
        private var trpeModel: TRPE? = null
        private val lugar: EditText = view.trpe_lugar_txt
        private val fechaRetorno: EditText = view.trpe_date_txt
        private val trpeRCV: RecyclerView = view.trpe_rcv_answers
        private val calendar = Calendar.getInstance()

        fun setUpView(cuestionario: Cuestionario?, itemAdapter: TRPEAdapter) {
            cuestionario?.let {
                fechaRetorno.isFocusable = false
                trpeRCV.visibility = View.VISIBLE
                testTitleTextView.text = it.descripcion
                cardContainer.visibility = if (testTitleTextView.isChecked) View.VISIBLE else View.GONE

                btnAgregar.setOnClickListener {
                    cardContainer.visibility = View.VISIBLE
                    btnAgregar.visibility = View.GONE

                    trpeModel = TRPE()
                }
                btnAceptar.setOnClickListener { _ ->
                    if (!lugar.text.isNullOrBlank() && !fechaRetorno.text.isNullOrBlank()) {
                        cardContainer.visibility = View.GONE
                        btnAgregar.visibility = View.VISIBLE
                        trpeModel?.let {exists ->
                            exists.lugar = lugar.text.toString()
                            exists.fechaRetorno = fechaRetorno.text.toString()
                            trpeResultList.add(exists)

                            it.alternativas?.get(0)?.let { alternativa ->
                                processResponse(testList, alternativa, true, trpeResultList.toString())
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
                    } else {
                        cardContainer.visibility = View.GONE
                        btnAgregar.visibility = View.GONE
                        trpeRCV.visibility = View.GONE
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

        fun setUpView(cuestionario: Cuestionario?) {
            cuestionario?.let {
                testTitleTextView.text = it.descripcion
            }
        }
    }

    inner class DATEEditTextListener: TextWatcher {
        private var cuestionario: Cuestionario? = null

        fun updateCuestionario(_cuestionario: Cuestionario?){
            cuestionario = _cuestionario
        }

        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            cuestionario?.alternativas?.get(0)?.let { alternativa ->
                try {
                    val result = SharedUtils.getWCDateformat(s.toString())
                    processResponse(testList, alternativa, true, result)
                } catch (ex: Exception) {
                    SharedUtils.showToast(context, context.getString(R.string.invalid_date))
                }
            }
        }
    }

    inner class TXTEditTextListener: TextWatcher {

        private var cuestionario: Cuestionario? = null

        fun updateCuestionario(_cuestionario: Cuestionario?){
            cuestionario = _cuestionario
        }

        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            cuestionario?.alternativas?.get(0)?.let { alternativa ->
                Log.w(TAG, "onTextChanged: ${s.toString()}")
                if (!s.toString().isBlank())
                    processResponse(testList, alternativa,true, s.toString())
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
                "TRAVPC" -> TRAVPC
                else -> TRPE //throw IllegalArgumentException("Tipo de data inválido: #$position")
            }
        }
        return -1
    }
}