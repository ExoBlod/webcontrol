package com.webcontrol.android.data

import android.view.View
import com.webcontrol.android.data.model.Alternativa

interface OnItemClickListener {

    fun onItemClick(position: Int, view: View?)

    fun onYesNOItemClick(position: Int, view: View?, alternativa: Alternativa)
}