package com.webcontrol.angloamerican.ui.checklistpreuso.adapters

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.Question

interface OnCardImgClickListener {
    fun onCardImgClick(question: Question, position: Int)
}