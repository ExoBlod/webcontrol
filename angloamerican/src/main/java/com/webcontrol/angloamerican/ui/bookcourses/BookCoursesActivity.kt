package com.webcontrol.angloamerican.ui.bookcourses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CourseContentData
import com.webcontrol.angloamerican.databinding.ActivityBookCoursesBinding
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import com.webcontrol.angloamerican.ui.bookcourses.ui.bookcourseshistory.BookCoursesHistoryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookCoursesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookCoursesBinding
    private val parentViewModel: BookCoursesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val idWorker = intent.getStringExtra("workerId")
        val idProgram = intent.getStringExtra("idEnterprise")
        val filterRC = intent.getIntExtra("filterRC", 0)
        parentViewModel.setReserveCourse(ReserveCourseData(
            workerId = idWorker!!,
            idEnterprise = idProgram!!,
            filterRC = filterRC
        ))
        title = resources.getString(R.string.title_book_courses)
    }
}