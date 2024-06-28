package com.webcontrol.angloamerican.ui.checklistpreuso.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.RowPreUsoTestChecklistVehInspectionBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeQuestionResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.Question
import com.webcontrol.angloamerican.ui.checklistpreuso.views.checklistfilling.SaveAnswersButtonListener
import com.webcontrol.angloamerican.utils.Utils

class ChecklistPreUsoFillingAdapter(private var questionList: List<Question>,
                                    private val onCardImgClickListener: OnCardImgClickListener,
                                    private val isConsulting: Boolean,
                                    private val saveAnswerButtonListener : SaveAnswersButtonListener,
                                    private val parentViewModel : CheckListPreUsoViewModel
) :
    RecyclerView.Adapter<ChecklistPreUsoFillingAdapter.ChecklisFillingtHolder>() {

    fun setList(_groupList: List<Question>) {
        questionList = _groupList
        notifyDataSetChanged()
    }

    fun getList() = questionList

    fun isCompleteCL():Boolean {
        questionList.forEach {
            if (it.answer == TypeAnswer.NN)
                return false
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklisFillingtHolder {
        val itemBinding = RowPreUsoTestChecklistVehInspectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChecklisFillingtHolder(itemBinding, parent.context, isConsulting,
            onCardImgClickListener, saveAnswerButtonListener, isCompleteCL(), parentViewModel.uiCheckListStatus)
    }
    override fun onBindViewHolder(holder: ChecklisFillingtHolder, position: Int) {
        val question: Question = questionList[position]
        holder.clearData()
        holder.bind(question, position)
    }

    override fun getItemCount(): Int = questionList.size

    class ChecklisFillingtHolder(private val itemBinding: RowPreUsoTestChecklistVehInspectionBinding,
                                 private val context: Context,
                                 private val isConsulting: Boolean,
                                 private val cardImgClickListener: OnCardImgClickListener,
                                 private val saveAnswersButtonListener: SaveAnswersButtonListener,
                                 private val isComplete: Boolean,
                                 private val status : Boolean
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(question: Question, position: Int) = with(itemBinding) {
            if(isConsulting && !status) {
                disableChildViews(itemBinding.cardContainer)
            }

            if(question.type == TypeQuestionResponse.responseYesOrNo) {
                itemBinding.btnNA.visibility = View.GONE
            }

            when(question.category){
                Question.YELLOW_STATUS -> {
                    itemBinding.yellowCardView.visibility = View.VISIBLE
                    itemBinding.redCardView.visibility = View.GONE
                    itemBinding.greenCardView.visibility = View.GONE
                }
                Question.RED_STATUS -> {
                    itemBinding.redCardView.visibility = View.VISIBLE
                    itemBinding.yellowCardView.visibility = View.GONE
                    itemBinding.greenCardView.visibility = View.GONE
                    cardImg.visibility = View.GONE
                }
                else -> {
                    itemBinding.greenCardView.visibility = View.VISIBLE
                    itemBinding.yellowCardView.visibility = View.GONE
                    itemBinding.redCardView.visibility = View.GONE
                }
            }

            lblTestTitle.text = question.name

            when(question.answer){
                TypeAnswer.NA -> {
                    btnYes.setBackgroundColor(Color.WHITE)
                    btnNo.setBackgroundColor(Color.WHITE)
                    btnNA.setBackgroundColor(Color.YELLOW)
                }
                TypeAnswer.NO -> {
                    btnYes.setBackgroundColor(Color.WHITE)
                    btnNo.setBackgroundColor(root.context.resources.getColor(R.color.lightRed))
                    btnNA.setBackgroundColor(Color.WHITE)
                    if(question.reqPhoto.equals("SI") && question.category != Question.RED_STATUS){
                        cardImg.visibility = View.VISIBLE
                        imageEvidence.visibility = View.VISIBLE
                        if(question.photo.isNotEmpty()){
                            val uri: Uri = Uri.parse(question.photo)
                            imageEvidence.setImageURI(uri)
                        }
                    }
                }
                TypeAnswer.SI -> {
                    btnYes.setBackgroundColor(Color.GREEN)
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
                question.photo = ""
                question.answer = TypeAnswer.SI
                cardImg.visibility = View.GONE
                btnYes.setBackgroundColor(Color.GREEN)
                btnNo.setBackgroundColor(Color.WHITE)
                btnNA.setBackgroundColor(Color.WHITE)
                if (question.isCritical)
                    saveAnswersButtonListener.onResponseChange(TypeAnswer.SI)
            }

            btnNo.setOnClickListener {
                question.answer =  TypeAnswer.NO
                btnYes.setBackgroundColor(Color.WHITE)
                btnNo.setBackgroundColor(root.context.resources.getColor(R.color.lightRed))
                btnNA.setBackgroundColor(Color.WHITE)

                if(question.reqPhoto.equals("SI")){
                    cardImg.visibility = View.VISIBLE
                    imageEvidence.visibility = View.VISIBLE
                    if(question.photo.isNotEmpty()){
                        val uri: Uri = Uri.parse(question.photo)
                        imageEvidence.setImageURI(uri)
                    }
                }
                if (question.isCritical){
                    saveAnswersButtonListener.onResponseChange(TypeAnswer.NO)
                    cardImg.visibility = View.GONE
                }
            }

            btnNA.setOnClickListener {
                question.photo = ""
                question.answer = TypeAnswer.NA
                cardImg.visibility = View.GONE
                btnYes.setBackgroundColor(Color.WHITE)
                btnNo.setBackgroundColor(Color.WHITE)
                btnNA.setBackgroundColor(Color.YELLOW)
            }
            cardImg.setOnClickListener {
                cardImgClickListener.onCardImgClick(question, position)
            }
        }

        private fun disableChildViews(viewGroup: ViewGroup) {
            itemBinding.cardContainer.isClickable = false
            itemBinding.cardContainer.isEnabled = false
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                child.isEnabled = false
                child.isClickable = false

                if (child is ViewGroup) {
                    disableChildViews(child)
                }
            }
        }

        fun clearData() = with(itemBinding) {
            cardContainer.isClickable = true
            cardContainer.isEnabled = true

            btnNA.visibility= View.VISIBLE

            yellowCardView.visibility = View.GONE
            redCardView.visibility = View.GONE
            greenCardView.visibility = View.GONE

            lblTestTitle.text = ""

            btnYes.setBackgroundColor(Color.WHITE)
            btnNo.setBackgroundColor(Color.WHITE)
            btnNA.setBackgroundColor(Color.WHITE)

            cardImg.visibility = View.GONE
            imageEvidence.visibility = View.GONE
        }
    }
}

