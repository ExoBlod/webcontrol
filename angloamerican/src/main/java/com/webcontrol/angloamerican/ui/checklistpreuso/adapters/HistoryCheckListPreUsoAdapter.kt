package com.webcontrol.angloamerican.ui.checklistpreuso.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.RowPreUsoInspectionHistoryListBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse
import java.text.SimpleDateFormat
import java.util.*

class HistoryCheckListPreUsoAdapter(private var historyList: List<HistoryResponse>, var buttonClickListener: OnButtonClickListener? = null) :
    RecyclerView.Adapter<HistoryCheckListPreUsoAdapter.HistoryCheckListPreUsoHolder>() {

    fun setList(_historyList: List<HistoryResponse>){
        historyList = _historyList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCheckListPreUsoHolder {
        val itemBinding = RowPreUsoInspectionHistoryListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryCheckListPreUsoHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HistoryCheckListPreUsoHolder, position: Int) {
        val paymentBean: HistoryResponse = historyList[position]
        holder.bind(paymentBean,buttonClickListener)
    }

    override fun getItemCount(): Int = historyList.size

    interface OnButtonClickListener {
        fun onButtonClick(data: HistoryResponse)
    }


    class HistoryCheckListPreUsoHolder(val itemBinding: RowPreUsoInspectionHistoryListBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(historyResponse: HistoryResponse, buttonClickListener: OnButtonClickListener? = null) = with(itemBinding){

            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
            val formattedDate = originalFormat.parse(historyResponse.checklistDate)?.let { targetFormat.format(it) } ?: ""
            tvDate.text = formattedDate
            txtPlate.text = if(historyResponse.plate.isNullOrEmpty()) "SIN PLACA" else historyResponse.plate
            txtDriver.text = historyResponse.workerName
            lblitemCountCheckList.text = historyResponse.checkingInHead.toString()
            messageContainer.setOnClickListener {
                buttonClickListener?.onButtonClick(historyResponse)
            }
            if(historyResponse.checklistStatus == null) historyResponse.checklistStatus = "E1"
            when(historyResponse.checklistStatus){
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
}