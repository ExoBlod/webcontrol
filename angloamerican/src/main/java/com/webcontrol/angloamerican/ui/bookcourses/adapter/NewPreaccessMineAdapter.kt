package com.webcontrol.angloamerican.ui.bookcourses.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.databinding.RowBookCoursesHistoryListBinding
import com.webcontrol.angloamerican.databinding.RowHistoryBookCoursesBinding
import java.text.SimpleDateFormat
import java.util.*

class NewPreaccessMineAdapter(
    private var historyList: List<HistoryBookCourseData>
) {
}