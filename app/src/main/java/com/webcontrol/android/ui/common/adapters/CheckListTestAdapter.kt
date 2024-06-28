package com.webcontrol.android.ui.common.adapters

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.databinding.RowQuestionDateBinding
import com.webcontrol.android.databinding.RowQuestionOmOsBinding
import com.webcontrol.android.databinding.RowQuestionTrpeBinding
import com.webcontrol.android.databinding.RowQuestionTxtBinding
import com.webcontrol.android.databinding.RowTestCheckListBinding
import com.webcontrol.android.ui.common.widgets.DatePickerFragment.Companion.newInstance
import com.webcontrol.android.util.QuestionsType
import java.util.*

class CheckListTestAdapter(private val mContext: Context, private val items: MutableList<CheckListTest_Detalle>, private val listener: CheckListAdapterListener, private val buttonsDisabled: Boolean, private val TipoChecklist: String, private val checkListener: OnCheckListener?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), TestAnswerAdapterListener {
    private var checkListTestDetail: CheckListTest_Detalle? = null
    private var opcionesList: List<String>? = null
    private val respuestasList: List<String> = ArrayList()
    private var testAnswersAdapter: TestAnswersAdapter? = null
    private lateinit var binding : RowQuestionOmOsBinding
    private lateinit var binding2 : RowQuestionTxtBinding
    private lateinit var binding3 : RowQuestionDateBinding
    private lateinit var binding4 : RowQuestionTrpeBinding
    private lateinit var binding5 : RowTestCheckListBinding
    fun add(s: String?, position: Int) {
        var position = position
        position = if (position == -1) itemCount else position
        val xd = CheckListTest_Detalle(
                0,
                "SN",
                0,
                s!!,
                s!!,
                0,
                0,
                false,
                false,
                ""
        )
        items.add(position, xd)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        if (position < itemCount) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.section_text)
    }

    inner class SNViewHolder(view: RowTestCheckListBinding) : RecyclerView.ViewHolder(binding5.root) {
        private val item: MessageWrapper? = null

        var containerButtons: LinearLayout = view.sekizbitSwitch
        var lblTestTitle: TextView = view.lblTestTitle
        var btnLeft: Button = view.sekizbitSwitchBtnLeft
        var btnRight: Button = view.sekizbitSwitchBtnRight
        var mySwitch: SekizbitSwitch
        fun bind(position: Int) {
            checkListTestDetail = items[position]
            val testDescripcion = checkListTestDetail!!.descripcion
            lblTestTitle.text = testDescripcion ?: "-"
            mySwitch.setOnChangeListener(null)
            if (!buttonsDisabled) {
                applyClickEvents(position)
            } else {
                mySwitch.setSelected(if (checkListTestDetail!!.isChecked) 1 else 0)
                btnRight.isClickable = false
                btnLeft.isClickable = false
            }
        }

        private fun applyClickEvents(position: Int) {
            mySwitch.setOnChangeListener(object : OnSelectedChangeListener {
                override fun onSelectedChange(sender: SekizbitSwitch?) {
                    if (sender!!.checkedIndex == 0) {
                        items[position].isChecked = false
                        items[position].btnSIActive =true
                        //listener.onClickButtonNO(position);
                    } else if (sender.checkedIndex == 1) {
                        //listener.onClickButtonSI(position);
                        items[position].isChecked = true
                        items[position].btnSIActive=true
                    }
                }
            })
        }

        init {
            mySwitch = SekizbitSwitch(view.sekizbitSwitch)
            if (TipoChecklist == "TFS" || TipoChecklist == "ENC" || TipoChecklist == "TMZ" || TipoChecklist =="COV" || TipoChecklist == "EQV" || TipoChecklist == "TPA"|| TipoChecklist == "DDS" || TipoChecklist == "VTP") {
                btnLeft.text = "NO"
                btnRight.text = "SI"
            }
        }
    }

    inner class OmOsViewHolder(view: RowQuestionOmOsBinding) : RecyclerView.ViewHolder(binding.root) {

        var lblTestTitle: TextView = view.lblTestTitle
        var rcvAnswers: RecyclerView = view.rcvAnswers

        fun bind(position: Int) {
            checkListTestDetail = items[position]
            val testDescripcion = checkListTestDetail!!.descripcion
            lblTestTitle.text = testDescripcion ?: "-"
            val layoutManager: RecyclerView.LayoutManager
            rcvAnswers.visibility = View.VISIBLE
            opcionesList = checkListTestDetail!!.getRespuestasConvert()
            //adaptador para preguntas de OM
            testAnswersAdapter = TestAnswersAdapter(mContext, items[position], this@CheckListTestAdapter, buttonsDisabled)
            layoutManager = LinearLayoutManager(mContext)
            rcvAnswers.layoutManager = layoutManager
            rcvAnswers.adapter = testAnswersAdapter
        }

        init {
        }
    }

    inner class TxtViewHolder(itemView: RowQuestionTxtBinding) : RecyclerView.ViewHolder(binding2.root) {
        var lblAnswer: EditText = itemView.lblAnswer
        var lblTestTitle: TextView = itemView.lblTestTitle
        fun bind(position: Int) {
            val testDescripcion = items[position].descripcion
            lblTestTitle.text = testDescripcion ?: "-"
        }

        init {
        }
    }

    inner class DateViewHolder(itemView: RowQuestionDateBinding) : RecyclerView.ViewHolder(binding3.root) {
        var lblAnswer: EditText = itemView.lblAnswer
        var lblTestTitle: TextView = itemView.lblTestTitle

        fun bind(position: Int) {
            val testDescripcion = items[position].descripcion
            lblTestTitle.text = testDescripcion ?: "-"
            lblAnswer.setOnClickListener {
                showDatePicker()
            }
        }
        fun showDatePicker() {
            val newFragment = newInstance(OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                lblAnswer.setText(selectedDate)
            })
            newFragment.show((mContext as AppCompatActivity).supportFragmentManager, "datePicker")
        }

        init {
        }
    }

    inner class TrpeTravViewHolder(itemView: RowQuestionTrpeBinding) : RecyclerView.ViewHolder(binding4.root) {

        var lblTestTitle: TextView = itemView.trpeSwitchTestTitle
        fun bind(position: Int) {
            val testDescripcion = items[position].descripcion
            lblTestTitle.text = testDescripcion ?: "-"
        }

        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        return when (viewType) {
            QuestionsType.TYPE_OM, QuestionsType.TYPE_OS -> {
                binding = RowQuestionOmOsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return  OmOsViewHolder(binding)
            }
            QuestionsType.TYPE_TXT -> {
                binding2 = RowQuestionTxtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return  TxtViewHolder(binding2)
            }
            QuestionsType.TYPE_DATE -> {
                binding3 = RowQuestionDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return  DateViewHolder(binding3)
            }
            QuestionsType.TYPE_TRPE, QuestionsType.TYPE_TRAV -> {
                binding4 = RowQuestionTrpeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return  TrpeTravViewHolder(binding4)
            }
            else -> {
                binding5 = RowTestCheckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return  SNViewHolder(binding5)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position].tipo != null) {
            when (items[position].tipo) {
                "OM", "OS" -> (holder as OmOsViewHolder).bind(position)
                "TXT" -> (holder as TxtViewHolder).bind(position)
                "DATE" -> (holder as DateViewHolder).bind(position)
                "TRPE", "TRAV" -> (holder as TrpeTravViewHolder).bind(position)
                else -> (holder as SNViewHolder).bind(position)
            }
        } else {
            (holder as SNViewHolder).bind(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return items[position].idTest.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position].tipo != null) {
            when (items[position].tipo) {
                "SN" -> return QuestionsType.TYPE_SN
                "OM" -> return QuestionsType.TYPE_OM
                "OS" -> return QuestionsType.TYPE_OS
                "TXT" -> return QuestionsType.TYPE_TXT
                "DATE" -> return QuestionsType.TYPE_DATE
                "TRAV" -> return QuestionsType.TYPE_TRAV
                "TRPE" -> return QuestionsType.TYPE_TRPE
                "CONT" -> return QuestionsType.TYPE_CONT
            }
        }
        return QuestionsType.TYPE_SN
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onRowItemClick(value: String?, item: CheckListTest_Detalle) {
        //actualiza las respuestas seleccionadas de la pregunta en la currentPosition
        if (checkListener != null) {
            //checkListTest_detalle = listCheckListTest_Detalle.get(currentPosition);
            if (value != null) {
                checkListTestDetail!!.respuestaSeleccionada = value
            }
            //listCheckListTest_Detalle.set(currentPosition, checkListTest_detalle);
            //checkListener.onCheck(value, currentPosition);
            App.db.checkListDao().updateValorSeleccionadoCheckListDetalle(checkListTestDetail!!.idDb, checkListTestDetail!!.groupId, checkListTestDetail!!.idTest, checkListTestDetail!!.idTest_Detalle, checkListTestDetail!!.respuestaSeleccionada)
        }
    }
}