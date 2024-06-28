package com.webcontrol.android.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.webcontrol.android.R
import com.webcontrol.android.databinding.AttendanceHostBinding
import com.webcontrol.android.ui.checklist.HistoricoCheckListFragment
import com.webcontrol.android.util.addFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceHostFragment: Fragment() {
    private lateinit var binding: AttendanceHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=this.requireArguments().getString("title")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AttendanceHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            addFragment(childFragmentManager, R.id.attendance_main_content, AttendanceHstFragment(), AttendanceHostFragment::class.java.simpleName)
        }
    }

}