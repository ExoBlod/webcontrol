package com.webcontrol.android.ui.covid.initialdata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.databinding.ContentDatosInicialesAntecedentesBinding
import com.webcontrol.android.ui.common.adapters.AntecedentesAdapter
import com.webcontrol.android.ui.common.adapters.AntecedentesAdapterListener
import com.webcontrol.android.ui.common.adapters.TypeFactory
import com.webcontrol.android.ui.common.adapters.TypeFactoryImpl
import com.webcontrol.android.ui.covid.DatosInicialesFragment
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AntecedentesFragment : Fragment(), AntecedentesAdapterListener {
    private lateinit var binding: ContentDatosInicialesAntecedentesBinding
    val viewModel: DatosInicialesSharedViewModel by viewModels()
    lateinit var listAntecedentes: List<Antecedentes>
    private lateinit var antecedentesAdapter: AntecedentesAdapter

    fun newInstance(aaa: String?): DatosInicialesFragment {
        val fragment = DatosInicialesFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentDatosInicialesAntecedentesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("prueba antecedentes", "fragment 5")
        loadData()
    }

    private fun loadData() {
        viewModel.getAntecedentes().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                listAntecedentes = result.sortedByDescending { it.tipo }
                setUIElements()
            } else
                SharedUtils.showToast(requireContext(), "null")
        }
    }

    private fun setUIElements() {
        viewModel.getCustomer().observe(viewLifecycleOwner) {
            if (it.equals("ANT"))
                binding.lblTitle.text = "ANTECEDENTES PATOLÓGICOS"
            else
                binding.lblTitle.text = "ANTECEDENTES"
        }
        val typeFactory: TypeFactory = TypeFactoryImpl()
        antecedentesAdapter = AntecedentesAdapter(requireContext(), listAntecedentes, typeFactory, this)
        binding.rcvAntecedentes.setHasFixedSize(true)
        binding.rcvAntecedentes.layoutManager = LinearLayoutManager(context)
        binding.rcvAntecedentes.adapter = antecedentesAdapter
    }

    override fun onClickButtonSI(item: Antecedentes) {
        for (i in listAntecedentes.indices) {
            if (listAntecedentes[i].codigo == item.codigo) {
                viewModel.user.value!!.listAntecedentes[i].isChecked = true
            }
        }
    }

    override fun onClickButtonNO(item: Antecedentes) {
        for (i in listAntecedentes.indices) {
            if (listAntecedentes[i].codigo == item.codigo) {
                viewModel.user.value!!.listAntecedentes[i].isChecked = false
            }
        }
    }

    override fun onTextChanged(item: Antecedentes){
        for (i in listAntecedentes.indices) {
            if (listAntecedentes[i].codigo == item.codigo) {
                Log.i("listener", item.comentario!!)
                viewModel.user.value!!.listAntecedentes[i].comentario = item.comentario
                listAntecedentes[i].comentario = item.comentario
                viewModel.user.value!!.listAntecedentes[i].isChecked = !item.comentario.isNullOrEmpty()
                listAntecedentes[i].isChecked = !item.comentario.isNullOrEmpty()
            }
        }
    }
}