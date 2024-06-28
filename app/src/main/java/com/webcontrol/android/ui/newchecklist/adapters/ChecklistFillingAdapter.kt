package com.webcontrol.android.ui.newchecklist.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.databinding.RowTestChecklistVehInspectionBinding
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.NewCheckListQuestion
import com.webcontrol.android.ui.newchecklist.data.TypeAnswer

class ChecklistFillingAdapter(private var questionList: List<NewCheckListQuestion>) :
    RecyclerView.Adapter<ChecklistFillingAdapter.ChecklisFillingtHolder>() {

    fun setList(_groupList: List<NewCheckListQuestion>) {
        questionList = _groupList
        notifyDataSetChanged()
    }

    fun getList() = questionList

    fun isCompleteCL():Boolean {

        questionList.forEach {
            if (  it.answer == null || it.answer == TypeAnswer.NN)
                return false
        }
        return true
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklisFillingtHolder {
        val itemBinding = RowTestChecklistVehInspectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChecklisFillingtHolder(itemBinding)
    }
    override fun onBindViewHolder(holder: ChecklisFillingtHolder, position: Int) {
        val question: NewCheckListQuestion = questionList[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int = questionList.size

    class ChecklisFillingtHolder(private val itemBinding: RowTestChecklistVehInspectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var answerSelected: String? = null

        fun bind(newCheckList: NewCheckListQuestion) = with(itemBinding) {
            lblTestTitle.text = newCheckList.nombre
            when(newCheckList.answer){
                TypeAnswer.NA -> {
                    btnYes.setBackgroundColor(Color.GRAY)
                    btnNo.setBackgroundColor(Color.GRAY)
                    btnNA.setBackgroundColor(Color.YELLOW)
                }
                TypeAnswer.NO -> {
                    btnYes.setBackgroundColor(Color.GRAY)
                    btnNo.setBackgroundColor(root.context.resources.getColor(R.color.lightRed))
                    btnNA.setBackgroundColor(Color.GRAY)
                }
                TypeAnswer.SI -> {
                    btnYes.setBackgroundColor(Color.GREEN)
                    btnNo.setBackgroundColor(Color.GRAY)
                    btnNA.setBackgroundColor(Color.GRAY)
                }
                TypeAnswer.NN -> {
                    btnYes.setBackgroundColor(Color.WHITE)
                    btnNo.setBackgroundColor(Color.WHITE)
                    btnNA.setBackgroundColor(Color.WHITE)
                }
                else -> {
                    btnYes.setBackgroundColor(Color.WHITE)
                    btnNo.setBackgroundColor(Color.WHITE)
                    btnNA.setBackgroundColor(Color.WHITE)
                }
            }
            btnYes.setOnClickListener {
                newCheckList.answer = TypeAnswer.SI
                newCheckList.valor = TypeAnswer.SI.name
                btnYes.setBackgroundColor(Color.GREEN)
                btnNo.setBackgroundColor(Color.GRAY)
                btnNA.setBackgroundColor(Color.GRAY)
            }

            btnNo.setOnClickListener {
                newCheckList.answer = TypeAnswer.NO
                newCheckList.valor =  TypeAnswer.NO.name
                btnYes.setBackgroundColor(Color.GRAY)
                btnNo.setBackgroundColor(root.context.resources.getColor(R.color.lightRed))
                btnNA.setBackgroundColor(Color.GRAY)
            }

            btnNA.setOnClickListener {
                newCheckList.answer = TypeAnswer.NA
                newCheckList.valor = TypeAnswer.NA.name
                btnYes.setBackgroundColor(Color.GRAY)
                btnNo.setBackgroundColor(Color.GRAY)
                btnNA.setBackgroundColor(Color.YELLOW)
            }
        }
    }
}
