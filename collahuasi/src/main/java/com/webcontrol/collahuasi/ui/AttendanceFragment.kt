package com.webcontrol.collahuasi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.webcontrol.collahuasi.R
import com.webcontrol.collahuasi.presentation.attendance.GetAttendanceViewModel
import com.webcontrol.core.common.model.Resource
import com.webcontrol.core.common.widgets.LoadingView
import kotlinx.android.synthetic.main.attendance_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class AttendanceFragment : Fragment() {
    private lateinit var binding: AttendanceFragment
    companion object {
        fun newInstance() = AttendanceFragment()
    }

    private lateinit var loader: LoadingView
    private val getAttendanceViewModel by viewModel<GetAttendanceViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.attendance_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = LoadingView(context)

        getAttendanceViewModel.getAttendance("46221041")

        observeAttendanceState()
    }

    private fun observeAttendanceState() {
        getAttendanceViewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    if (state.isLoading) loader.show() else loader.dismiss()
                }
                is Resource.Success -> {
                    loader.dismiss()
                    if (state.data!!.isNotEmpty()) {
                        emptyView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    loader.dismiss()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}