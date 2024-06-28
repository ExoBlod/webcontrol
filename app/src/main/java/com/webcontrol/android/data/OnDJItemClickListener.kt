package com.webcontrol.android.data

import android.view.View

interface OnDJItemClickListener {

    fun onItemClick(position: Int, view: View?, data: String)
}