package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.data.network.response.AnswerQuestionData
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.databinding.RowTestBookingCourseMultipleOptionBinding
import com.webcontrol.angloamerican.databinding.RowTestBookingCourseSimpleOptionBinding
import com.webcontrol.angloamerican.databinding.RowTestBookingCourseTrueFalseBinding

class QuestionCourseAdapter(
    private var listQuestions: List<QuestionData>,
    var onItemAnswerClickListener: OnButtonClickAnswerListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setList(_listQuestion: List<QuestionData>) {
        listQuestions = _listQuestion
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SIMPLE_OPTION ->
                SimpleOptionViewHolder(
                    RowTestBookingCourseSimpleOptionBinding.inflate(inflater, parent, false)
                )

            VIEW_TYPE_TRUE_FALSE ->
                TrueFalseViewHolder(
                    RowTestBookingCourseTrueFalseBinding.inflate(inflater, parent, false)
                )

            VIEW_TYPE_MULTIPLE_OPTION ->
                MultipleOptionViewHolder(
                    RowTestBookingCourseMultipleOptionBinding.inflate(inflater, parent, false)
                )

            else ->
                throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }


    override fun getItemCount(): Int {
        return listQuestions.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val question = listQuestions[position]

        when (holder) {
            is SimpleOptionViewHolder -> holder.bind(question)
            is TrueFalseViewHolder -> holder.bind(question)
            is MultipleOptionViewHolder -> holder.bind(question)
        }
    }

    inner class SimpleOptionViewHolder(private val binding: RowTestBookingCourseSimpleOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionData) {
            with(binding) {
                val answerQuestionData: List<AnswerQuestionData> = question.answerQuestionCourses
                linearQuestionOptions.removeAllViews()
                val radioGroup = RadioGroup(root.context)
                linearQuestionOptions.addView(radioGroup)
                val apiQuestion = question.question
                val correctQuestion = apiQuestion.replace(Regex("\\*|<br/>|<BR/>|//n|//N"), "\n")
                txtQuestion.text = correctQuestion
                for (i in answerQuestionData.indices) {
                    val radioButton = RadioButton(root.context)
                    radioButton.id = View.generateViewId()
                    if (answerQuestionData[i].answerMarked) {
                        radioButton.isChecked = true
                    }
                    radioButton.text = answerQuestionData[i].answer
                    radioButton.setOnClickListener {
                        onItemAnswerClickListener.onItemAnswerClick(
                            answerQuestionData[i].answerId,
                            question.questionType,
                            true
                        )
                    }
                    radioGroup.addView(radioButton)
                }
            }
        }
    }

    inner class TrueFalseViewHolder(private val binding: RowTestBookingCourseTrueFalseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionData) {
            with(binding) {
                val answerQuestionData: List<AnswerQuestionData> = question.answerQuestionCourses
                linearQuestionTrueFalseOptions.removeAllViews()
                val radioGroup = RadioGroup(root.context)
                linearQuestionTrueFalseOptions.addView(radioGroup)
                val apiQuestion = question.question
                val correctQuestion = apiQuestion.replace(Regex("\\*|<br/>|<BR/>|//n|//N"), "\n")
                lblTestTitle.text = correctQuestion
                for (i in answerQuestionData.indices) {
                    val radioButton = RadioButton(root.context)
                    radioButton.id = View.generateViewId()
                    if (answerQuestionData[i].answerMarked) {
                        radioButton.isChecked = true
                    }
                    radioButton.text = answerQuestionData[i].answer
                    radioButton.setOnClickListener {
                        onItemAnswerClickListener.onItemAnswerClick(
                            answerQuestionData[i].answerId,
                            question.questionType,
                            true
                        )
                    }
                    radioGroup.addView(radioButton)
                }
            }
        }
    }

    inner class MultipleOptionViewHolder(private val binding: RowTestBookingCourseMultipleOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionData) {
            with(binding) {
                val answerQuestionData: List<AnswerQuestionData> = question.answerQuestionCourses
                linearQuestionMultipleOption.removeAllViews()
                val checkBoxGroup = LinearLayout(root.context)
                checkBoxGroup.orientation = LinearLayout.VERTICAL
                linearQuestionMultipleOption.addView(checkBoxGroup)
                val apiQuestion = question.question
                val correctQuestion = apiQuestion.replace(Regex("\\*|<br/>|<BR/>|//n|//N"), "\n")
                lblTestTitle.text = correctQuestion
                for (i in answerQuestionData.indices) {
                    val checkBox = CheckBox(root.context)
                    checkBox.text = answerQuestionData[i].answer
                    if (answerQuestionData[i].answerMarked) {
                        checkBox.isChecked = true
                    }
                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        onItemAnswerClickListener.onItemAnswerClick(
                            answerQuestionData[i].answerId,
                            question.questionType,
                            isChecked
                        )
                    }
                    checkBoxGroup.addView(checkBox)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val question = listQuestions[position]
        return when (question.questionType) {
            1 -> VIEW_TYPE_TRUE_FALSE
            2 -> VIEW_TYPE_SIMPLE_OPTION
            3 -> VIEW_TYPE_MULTIPLE_OPTION
            else -> throw IllegalArgumentException("Unknown viewType: ${question.questionType}")
        }
    }

    interface OnButtonClickAnswerListener {
        fun onItemAnswerClick(answerId: Int, questionType: Int, isChecked: Boolean)
    }

    companion object {
        private const val VIEW_TYPE_TRUE_FALSE = 1
        private const val VIEW_TYPE_SIMPLE_OPTION = 2
        private const val VIEW_TYPE_MULTIPLE_OPTION = 3
    }
}