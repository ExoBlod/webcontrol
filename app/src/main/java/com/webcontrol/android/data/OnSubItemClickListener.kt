package com.webcontrol.android.data

import android.view.View
import com.webcontrol.android.data.model.Alternativa

interface OnSubItemClickListener {
    fun onOMItemClick(position: Int, view: View?, alternativa: Alternativa)

    fun onOMTextChanged(position: Int, alternativa: Alternativa, text: String)
}