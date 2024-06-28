package com.webcontrol.android.ui.preacceso

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.databinding.FragmentPreaccesoHistoricoBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.adapters.PreaccesoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ControlPreaccesoFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentPreaccesoHistoricoBinding
    private var pAdapter: PreaccesoAdapter? = null

    private lateinit var rvPreaccess: RecyclerView
    private lateinit var client: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=requireArguments().getString(title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.fragment_preacceso_historico, container, false)
        binding = FragmentPreaccesoHistoricoBinding.inflate(inflater, container, false)
        //rvPreaccess = view.findViewById(R.id.rv_preaccessList)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))

        binding.rvPreaccessList.addItemDecoration(divider)
        binding.rvPreaccessList.layoutManager = LinearLayoutManager(requireContext())

        setArgs()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun setArgs() {
        client = requireArguments().getString(CLIENT, "")
    }

    private fun setUI() {
        binding.btnNuevo.setOnClickListener {
            startActivity(Intent(context, CabeceraActivity::class.java).apply {
                putExtra(CLIENT, client)
            })
        }
    }

    fun load() {
        val preaccessList = App.db.preaccesoDao().all()
        if (preaccessList.isNotEmpty()) {
            binding.emptyState.visibility = View.GONE
            binding.rvPreaccessList.visibility = View.VISIBLE
            pAdapter = PreaccesoAdapter(requireContext(), preaccessList)
            binding.rvPreaccessList.adapter = pAdapter
            binding.txtNumeroRegistros.text = preaccessList.size.toString()
        } else {
            binding.rvPreaccessList.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }

    companion object {
        const val CLIENT = "CLIENT"
        const val title = "title"

    }
}