package com.webcontrol.android.angloamerican.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.R
import com.webcontrol.android.common.GenericAdapter
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialQuellavecoBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.angloamerican.common.TypeFactoryCourse
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
open class QuellavecoCredentialFragment : Fragment() {

    private var _binding: FragmentCredentialQuellavecoBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModels<CoursesCredentialViewModel>()
    lateinit var worker: WorkerAnglo

    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet

    private var isFront = false
    private var isValid = true

    companion object {
        const val title = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = requireArguments().getString(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCredentialQuellavecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimation()
        initBinding()
        loadData()
        setUserPhoto()
        observeCoursesState()
    }

    private fun initAnimation() {
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet

        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet
    }

    private fun initBinding() {
        val scale: Float = requireContext().resources.displayMetrics.density
        with(binding) {
            includefront.cardFrontQv.cameraDistance = 8000 * scale
            includeback.cardBackQv.cameraDistance = 8000 * scale

            btnFlip.setOnClickListener {
                btnFlip.hide()
                flipCard()
                handler.postDelayed({
                    binding.btnFlip.show()
                }, 1000)
            }
        }
    }

    private fun flipCard() {
        with(binding) {
            includeback.cardBackQv.alpha = 1f
            if (isValid) {
                isFront = if (isFront) {
                    frontAnim.setTarget(includefront.cardFrontQv)
                    backAnim.setTarget(includeback.cardBackQv)
                    frontAnim.start()
                    backAnim.start()
                    false
                } else {
                    frontAnim.setTarget(includeback.cardBackQv)
                    backAnim.setTarget(includefront.cardFrontQv)
                    backAnim.start()
                    frontAnim.start()
                    true
                }
            }
        }
    }

    private fun loadData() {
        SharedUtils.showLoader(context, getString(R.string.loading))
        val api: RestInterfaceAnglo = RestClient.buildAnglo()
        val call = api.getWorker(
            object : HashMap<String, String>() {
                init {
                    put("WorkerId", SharedUtils.getUsuarioId(context))
                }
            }
        )
        call.enqueue(object : Callback<ApiResponseAnglo<WorkerAnglo>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<WorkerAnglo>>,
                response: Response<ApiResponseAnglo<WorkerAnglo>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        worker = response.body()!!.data
                        setUIElements()
                    } else {
                        setUISinCredential()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<WorkerAnglo>>, t: Throwable) {
                t.printStackTrace()
                Snackbar.make(view!!, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry)) { loadData() }.show()
                SharedUtils.dismissLoader(context)
            }
        })

        viewModel.getCourses(SharedUtils.getUsuarioId(context))
    }

    private fun setUIElements() {
        with(binding) {
            if (worker.credencial != null) {
                if (includefront.imageAvatar != null) {
                    includefront.imageAvatar.visibility = View.VISIBLE
                }
                includefront.lblName.text = "${worker.nombre} ${worker.apellidos}"
                includefront.lblId.text = SharedUtils.FormatRut(worker.id)
                includefront.lblEmpresa.text = worker.companiaNombre
                includefront.lblPosition.text = "${"CARGO"}: " + "${(worker!!.cargo)}"
                includefront.lblAuthorized.text =
                    "${"AUTORIZADO"}: ${if (worker.autor) "SI" else "NO"}"

            } else {
                setUISinCredential()
            }
        }

        SharedUtils.dismissLoader(context)
    }

    private fun setUserPhoto() {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(
                urlPhoto, getString(R.string.ws_url_mensajeria),
                SharedUtils.getUsuarioId(context)
            )
        Glide.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .into(binding.includefront.imageAvatar)
    }

    private fun setUISinCredential() {
        binding.includefront.lblName.text =
            "El trabajador con ID ${SharedUtils.getUsuarioId(context)} no cuenta con credencial activa"
        binding.includefront.lblAuthorized.visibility = View.GONE
    }

    private fun observeCoursesState() {
        with(binding) {
            viewModel.coursesState.observe(viewLifecycleOwner) { its ->
                when (its) {
                    is Resource.Loading -> {
                        // show loader
                    }
                    is Resource.Success -> {
                        its.data?.let {
                            if (it.isNotEmpty()) {
                                val layoutManager = LinearLayoutManager(context)
                                includeback.rvCourses.layoutManager = layoutManager
                                val adapter = GenericAdapter(TypeFactoryCourse())
                                includeback.rvCourses.adapter = adapter
                                adapter.setItems(it)
                            } else {
                                btnFlip.isEnabled = false
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), its.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}