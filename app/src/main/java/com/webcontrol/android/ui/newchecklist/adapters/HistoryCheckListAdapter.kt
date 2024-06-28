package com.webcontrol.android.ui.newchecklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.databinding.RowInspectionHistoryListBinding
import com.webcontrol.android.databinding.RowVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.data.NewCheckListHistory
import com.webcontrol.android.ui.newchecklist.data.NewCheckListQuestion
import java.text.SimpleDateFormat
import java.util.*

class HistoryCheckListAdapter(private var historyList: List<NewCheckListHistory>,var buttonClickListener: OnButtonClickListener? = null) :
    RecyclerView.Adapter<HistoryCheckListAdapter.HistoryCheckListHolder>() {

    fun setList(_historyList: List<NewCheckListHistory>){
        historyList = _historyList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCheckListHolder {
        val itemBinding = RowInspectionHistoryListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryCheckListHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HistoryCheckListHolder, position: Int) {
        val paymentBean: NewCheckListHistory = historyList[position]
        holder.bind(paymentBean,buttonClickListener)
    }

    override fun getItemCount(): Int = historyList.size

    interface OnButtonClickListener {
        fun onButtonClick(data: NewCheckListHistory)
    }


    class HistoryCheckListHolder(val itemBinding: RowInspectionHistoryListBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(newCheckListHistory: NewCheckListHistory, buttonClickListener: OnButtonClickListener? = null) = with(itemBinding){

            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
            val formattedDate = originalFormat.parse(newCheckListHistory.checklistDate)?.let { targetFormat.format(it) } ?: ""
            lblDate.text = formattedDate
            txtPlate.text = if(newCheckListHistory.placa.isNullOrEmpty()) "SIN PLACA" else newCheckListHistory.placa
            txtDriver.text = newCheckListHistory.workerName
            lblitemCountCheckList.text = newCheckListHistory.checklistInstanceId.toString()
            btnSearchInspection.setOnClickListener {
                buttonClickListener?.onButtonClick(newCheckListHistory)
            }
            if(newCheckListHistory.status == null) newCheckListHistory.status = "E1"
            when(newCheckListHistory.status){
                "E4" -> {
                    lblState.text = "Enviado con Observación Critica"
                    lblState.setTextColor(itemBinding.root.context.resources.getColor(R.color.red))
                }
                "E3" -> {
                    lblState.text = "Enviado con Observación"
                    lblState.setTextColor(itemBinding.root.context.resources.getColor(R.color.orange))
                }
                "E2" -> {
                    lblState.text = "Enviado Aprobado"
                    lblState.setTextColor(itemBinding.root.context.resources.getColor(R.color.green))
                }
                else -> {
                    lblState.text = "Pendiente"
                    lblState.setTextColor(itemBinding.root.context.resources.getColor(R.color.icon_tint_selected))
                }
            }
        }
    }

    class ListChecklist(val itemBinding: RowVehicularInspectionBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bindList(setCheckList: NewCheckListQuestion, buttonClickListener: OnButtonClickListener? = null) = with(itemBinding) {
            lblCountCheckList.text = setCheckList.nombrecheckgroup
        }
    }
}