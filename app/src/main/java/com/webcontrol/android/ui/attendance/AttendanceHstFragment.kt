package com.webcontrol.android.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentAttendanceHstBinding
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.replaceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceHstFragment: Fragment() {

    private lateinit var binding: FragmentAttendanceHstBinding
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var adapter: AttendanceListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAttendanceHstBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AttendanceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AttendanceListAdapter()

        initializeRecyclerView()
        setupUI()
        setupObservers()
        getData()
    }

    private fun setupUI() {
        binding.loaderMain.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark)
        binding.loaderMain.setOnRefreshListener {
            getData()
        }
        setFabAction()
    }

    private fun getData() {
        viewModel.getAttendanceHst(SharedUtils.getUsuarioId(requireContext()))
    }

    private fun setupObservers() {
        observeAttendanceHst()
    }

    private fun observeAttendanceHst() {
        viewModel.attendanceHstState().observe(viewLifecycleOwner) {
            if (it.isLoading) {
                showLoader()
            } else {
                dismissLoader()
                binding.loaderMain.isRefreshing = false
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess) {
                        if (response.data.isNotEmpty()) {
                            adapter.clear()
                            adapter.addItems(response.data)
                            showRecyclerList()
                        } else {
                            showEmptyList()
                        }
                    } else {
                        SharedUtils.showToast(requireContext(), response.message)
                    }
                } else if (it.data == null && it.error != null) {
                    showEmptyList()
                    SharedUtils.showToast(requireContext(), it.error)
                }
            }
        }
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(requireContext(), R.raw.loaddinglottie, "Cargando...", 0, 500, 200)
        binding.loaderMain.isRefreshing = true
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(activity)
        binding.loaderMain.isRefreshing = false
    }

    private fun showRecyclerList() {
        binding.emptyContainer.visibility = View.GONE
        binding.testRecyclerView.visibility=View.VISIBLE
    }

    private fun showEmptyList() {
        binding.emptyContainer.visibility = View.VISIBLE
        binding.testRecyclerView.visibility=View.GONE
    }

    private fun initializeRecyclerView() {
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        binding.testRecyclerView.layoutManager= LinearLayoutManager(activity)
        binding.testRecyclerView.addItemDecoration(divider)
        binding.testRecyclerView.adapter =adapter
    }

    private fun setFabAction() {
        binding.fabNewTest.setOnClickListener{
            replaceFragment(parentFragmentManager, R.id.attendance_main_content, AttendanceFragment(), AttendanceHstFragment::class.java.simpleName)
        }
    }
}