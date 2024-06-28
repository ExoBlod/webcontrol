package com.webcontrol.android.ui.newchecklist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.databinding.RowVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.TypeAnswer
import com.webcontrol.android.ui.newchecklist.listeners.ChecklistGroupListener

class ListChecklistAdapter(private var groupList: List<NewCheckListGroup>, var listener : ChecklistGroupListener) :
    RecyclerView.Adapter<ListChecklistAdapter.ListChecklistHolder>() {

    fun setList(_groupList: List<NewCheckListGroup>){
        groupList = _groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListChecklistHolder {
        val itemBinding = RowVehicularInspectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListChecklistHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListChecklistHolder, position: Int) {
        val groupListPos: NewCheckListGroup = groupList[position]
        holder.itemView.setOnClickListener {
            listener.onItemClickGroup(position)
        }
        var type = TypeAnswer.NN
        Log.e("ingresooom",groupListPos.isComplete().toString())
        if (groupListPos.isComplete()){
            type = TypeAnswer.SI
            groupListPos.data.forEach {
                if(it.answer == TypeAnswer.NO && it.critico){
                    type = TypeAnswer.NO
                    return@forEach
                } else if(it.answer == TypeAnswer.NO){
                    type = TypeAnswer.NA
                }
            }
        }
        holder.bind(groupListPos,type)
    }

    override fun getItemCount(): Int = groupList.size

    class ListChecklistHolder(private val itemBinding: RowVehicularInspectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(newCheckList: NewCheckListGroup, type:TypeAnswer) = with(itemBinding) {
            lblCountCheckList.text = newCheckList.nameGroup
            when(type){
                TypeAnswer.SI -> {
                    lblCountCheckList.setBackgroundColor(root.resources.getColor(R.color.color_green))
                }
                TypeAnswer.NA -> {
                    lblCountCheckList.setBackgroundColor(root.resources.getColor(R.color.orange))
                }
                TypeAnswer.NO -> {
                    lblCountCheckList.setBackgroundColor(root.resources.getColor(R.color.red))
                }
                TypeAnswer.NN -> {
                    lblCountCheckList.setBackgroundColor(root.resources.getColor(R.color.gray))
                }
            }
        }
    }
}
