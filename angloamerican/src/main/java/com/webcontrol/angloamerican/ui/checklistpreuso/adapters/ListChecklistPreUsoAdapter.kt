package com.webcontrol.angloamerican.ui.checklistpreuso.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.RowPreUsoVehicularInspectionBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListGroup
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse

class ListChecklistAdapter(private var groupList: List<QuestionListResponse>, var listener : ChecklistGroupListener, private val isCleaner: Boolean) :
    RecyclerView.Adapter<ListChecklistAdapter.ListChecklistHolder>() {

    fun setList(_groupList: List<QuestionListResponse>){
        groupList = _groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListChecklistHolder {
        val itemBinding = RowPreUsoVehicularInspectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListChecklistHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListChecklistHolder, position: Int) {
        val groupListPos: QuestionListResponse = groupList[position]
        holder.itemView.setOnClickListener {
            listener.onItemClickGroup(position)
        }

        var type = TypeAnswer.NN

        if (groupListPos.isComplete()){
            type = TypeAnswer.SI
            groupListPos.questions.forEach {
                if(it.answer == TypeAnswer.NO && it.isCritical){
                    type = TypeAnswer.NO
                    return@forEach
                } else if(it.answer == TypeAnswer.NO){
                    type = TypeAnswer.NA
                }
            }
        }
        holder.bind(groupListPos, type, isCleaner)
    }

    override fun getItemCount(): Int = groupList.size

    class ListChecklistHolder(private val itemBinding: RowPreUsoVehicularInspectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(newCheckList: QuestionListResponse, type:TypeAnswer, isCleaner : Boolean) = with(itemBinding) {
            if (isCleaner){
                cardContainer.setCardBackgroundColor(root.resources.getColor(R.color.white))
            }
            lblCountCheckList.text = newCheckList.nameGroup
            when(type){
                TypeAnswer.SI -> {
                    cardContainer.setCardBackgroundColor(root.resources.getColor(R.color.card_background_color_green))
                }
                TypeAnswer.NA -> {
                    cardContainer.setCardBackgroundColor(root.resources.getColor(R.color.card_background_color_orange))
                }
                TypeAnswer.NO -> {
                    cardContainer.setCardBackgroundColor(root.resources.getColor(R.color.card_background_color_red))
                }
                TypeAnswer.NN -> {
                    cardContainer.setCardBackgroundColor(root.resources.getColor(R.color.white))
                }
            }
        }
    }
}

interface ChecklistGroupListener {
    fun onItemClickGroup(position: Int)
}
