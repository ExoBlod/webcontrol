package com.webcontrol.android.ui.owndoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.button.MaterialButton
import com.webcontrol.android.R
import com.webcontrol.android.data.network.dto.OwnDocs
import com.webcontrol.android.databinding.FragmentOwnDocBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class OwnDocFragment : Fragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentOwnDocBinding
    private lateinit var btnUpdateDoc: MaterialButton
    private lateinit var rvListUpdateDoc: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.own_document)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOwnDocBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUpdateDoc = view.findViewById(R.id.btn_update_doc)
        rvListUpdateDoc = view.findViewById(R.id.rv_list_update_docs)

        btnUpdateDoc.setOnClickListener {
            rvListUpdateDoc.visibility = View.GONE
            (activity as MainActivity?)!!.navigateTo(
                UpdateDocFragment.newInstance(
                    getString(R.string.upload_file)
                ), getString(R.string.upload_file), 0
            )
        }

        ShowLoader()
        val api = RestClient.buildWebControlDocs()
        val call = api.getOwnDocs(SharedUtils.getUsuarioId(context))
        call.enqueue(object : retrofit2.Callback<List<OwnDocs>> {
            override fun onFailure(call: Call<List<OwnDocs>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), getString(R.string.error_getting_credentials))
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<List<OwnDocs>>, response: Response<List<OwnDocs>>) {
                if (response.isSuccessful) {
                    val listTypeDocDTO = response.body()!!
                    val adapter = UpdateDocsAdapter(
                        listTypeDocDTO,
                        requireActivity()
                    , onClickContainer = {onClickItem(it)})
                    rvListUpdateDoc.adapter = adapter
                    DismissLoader()
                }
                SharedUtils.dismissLoader(requireContext())
            }
        })
    }

    private fun onClickItem(ownDocs: OwnDocs){
        rvListUpdateDoc.visibility = View.GONE
        btnUpdateDoc.visibility = View.GONE
        (activity as MainActivity?)!!.navigateTo(
            OwnDocDetailsFragment.newInstance(
                getString(R.string.upload_file),
                ownDocs.ID
            ), getString(R.string.upload_file), 0
        )
        //findNavController().navigate(R.id.ownDocDetailsFragment, bundleOf("document" to ownDocs.ID))
    }

    private fun ShowLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            "Cargando...",
            0,
            500,
            200
        )
    }

    private fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    companion object {

        fun newInstance(param1: String) =
            OwnDocFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}