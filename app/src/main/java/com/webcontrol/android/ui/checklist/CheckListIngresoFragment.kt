package com.webcontrol.android.ui.checklist

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.model.Local
import com.webcontrol.android.databinding.FragmentIngresoCheckListBinding
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.FormatRut
import com.webcontrol.android.util.SharedUtils.getUsuario
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.setCompanyCheckList
import com.webcontrol.android.util.SharedUtils.setOSTCheckList
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.util.SharedUtils.wCDate
import com.webcontrol.android.vm.CheckListIngresoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CheckListIngresoFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentIngresoCheckListBinding
    val checkListIngresoViewModel: CheckListIngresoViewModel by viewModels()
    private var TipoCheckList: String? = null
    private var NomCheckList: String? = null
    private var idChecklist: String? = null
    private var idCompany: String? = null
    var listDivisiones: MutableList<Division>? = null
    var listLocales: MutableList<Local>? = null
    var checkListTest: CheckListTest? = null
    var isLoadLastregister = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoCheckList = requireArguments().getString("TipoCheckList")
        NomCheckList = requireArguments().getString("NomCheckList")
        idChecklist = requireArguments().getString("idChecklist")
        idCompany = SharedUtils.getIdCompany(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngresoCheckListBinding.inflate(inflater, container, false)
        initViewModel(binding)
        initData()
        loadLastData()
        validateTipoCheckList()
        binding.animationView.enableMergePathsForKitKatAndAbove(true)

        binding.btnSiguienteFf.setOnClickListener {
            btnSiguiente()
        }

        binding.btnValidarPatente.setOnClickListener {
            btnValidarPatente()
        }

        return binding.root
    }
    private fun validateTipoCheckList() {
        if (TipoCheckList == "ENC" || TipoCheckList == "COV" || TipoCheckList == "TMZ" || TipoCheckList == "EQV" || TipoCheckList == "DDS") {
            guardar(false)
            findNavController().navigate(
                R.id.checkListTestFragment, bundleOf(
                    "TipoCheckList" to TipoCheckList,
                    "NomCheckList" to NomCheckList, "idChecklist" to idChecklist
                )
            )
            ProgressLoadingJIGB.finishLoadingJIGB(context)
        }
    }

    private fun loadLastData() {
        val idcheckListLast = App.db.checkListDao().getLastInsertedCheckListTestId(TipoCheckList)
        if (idcheckListLast > 0) {
            checkListTest = App.db.checkListDao().selectCheckListById(idcheckListLast)
            enableControls(true)
            checkListIngresoViewModel.txtPatente.postValue(checkListTest!!.vehicleId)
            checkListIngresoViewModel.ddlDivisionIdSelected.postValue(checkListTest!!.divisionId)
            checkListIngresoViewModel.ddlLocalIdSelected.postValue(checkListTest!!.localId)
        } else isLoadLastregister = true
    }

    private fun initData() {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(urlPhoto, getString(R.string.ws_url_mensajeria), getUsuarioId(context))
        GlideApp.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .into(binding.imgUser)
        checkListIngresoViewModel.txtUserNames.postValue(getUsuario(context))
        binding.txtUserNames.text = getUsuario(context)
        checkListIngresoViewModel.txtUserRut.postValue(getUsuarioId(context))
        binding.txtRut.text = FormatRut(getUsuarioId(context))
        binding.txtPatente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.btnValidarPatente.visibility = View.VISIBLE
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.btnValidarPatente.visibility = View.VISIBLE
                if (charSequence.isNotEmpty()) {
                    if (charSequence.toString().contains("-") ||
                        charSequence.toString().contains(".") ||
                        charSequence.toString().contains(" ")) {
                        binding.txtPatenteError.isErrorEnabled = true
                        binding.txtPatenteError.error = "No debe ingresar caracteres especiales ni espacios"
                        binding.txtPatenteError.errorIconDrawable = null
                        binding.btnValidarPatente.isEnabled = false
                        binding.btnValidarPatente.background =
                            ContextCompat.getDrawable(context!!, R.drawable.round_bottom_disabled)

                    } else {
                        binding.txtPatenteError.isErrorEnabled = false
                        binding.txtPatenteError.error = null
                        binding.btnValidarPatente.isEnabled = true
                        binding.btnValidarPatente.background =
                            ContextCompat.getDrawable(context!!, R.drawable.round_bottom)
                        binding.txtPatenteError.error = null
                    }

                } else {
                    binding.btnValidarPatente.isEnabled = false
                    binding.btnValidarPatente.background =
                        ContextCompat.getDrawable(context!!, R.drawable.round_bottom_disabled)
                }
                checkListIngresoViewModel.seValidoPatente.postValue(false)
                binding.animationView.visibility = View.GONE
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        listLocales = ArrayList()
        listLocales!!.add(Local("SIN_LOCAL", "SIN LOCAL"))
        val adapterLocal = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            listLocales!!
        )
        binding.ddlLocal.setAdapter(adapterLocal)

        listDivisiones = ArrayList()
        listDivisiones!!.add(Division("SIN DIVISION", "SIN DIVISION"))
        val adapterDivision = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            listDivisiones!!
        )
        binding.ddlDivision.setAdapter(adapterDivision)

        enableControls(false)
        loadEmpresas()
        loadDivisiones()

        binding.ddlDivision.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                binding.btnValidarPatente.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom)
                binding.animationView.visibility = View.INVISIBLE
                binding.btnValidarPatente.visibility = View.VISIBLE
                checkListIngresoViewModel.ddlDivisionIdSelected.postValue(listDivisiones!![position].id)
                enableControls(true)
                binding.ddlLocal.setText("")
                if (binding.txtPatente.text!!.isEmpty()) {
                    binding.btnValidarPatente.isEnabled = false
                    binding.btnValidarPatente.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_bottom_disabled
                    )
                }
                listLocales = ArrayList()
                listLocales!!.add(Local("SIN_LOCAL", "SIN LOCAL"))
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    listLocales!!
                )
                binding.ddlLocal.setAdapter(adapter)
                loadLocal(listDivisiones!![position].id)
            }
        binding.ddlLocal.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->
                checkListIngresoViewModel.ddlLocalIdSelected.postValue(listLocales!![position].id)
            }
    }

    private fun enableControls(state: Boolean) {
        binding.txtPatente.isEnabled = state
        binding.btnValidarPatente.isEnabled = state
    }

    private fun initViewModel(fragmentFatigaBinding: FragmentIngresoCheckListBinding) {
        fragmentFatigaBinding.vm = checkListIngresoViewModel
        //validacion patente
        checkListIngresoViewModel.getValidarPatenteResult()
            .observe(viewLifecycleOwner, Observer { result ->
                binding.ddlDivision.dismissDropDown()
                binding.ddlLocal.dismissDropDown()
                binding.ddlDivision.isClickable = false
                binding.animationView.pauseAnimation()
                if (result.isSuccess) {
                    if (result.data.isAutor) {
                        binding.btnValidarPatente.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_bottom_green
                        )
                        checkListIngresoViewModel.txtPatenteError.postValue("")
                        checkListIngresoViewModel.seValidoPatente.postValue(true)
                        binding.animationView.setAnimation(R.raw.spinner_done)
                        binding.animationView.repeatCount = 0
                        binding.animationView.playAnimation()
                        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.INVISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.VISIBLE
                                binding.txtPatente.isEnabled = true
                                binding.ddlDivision.isClickable = true
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.VISIBLE
                            }

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                    } else {
                        binding.btnValidarPatente.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom_red)
                        checkListIngresoViewModel.txtPatenteError.postValue("La patente no esta autorizada")
                        checkListIngresoViewModel.seValidoPatente.postValue(false)
                        binding.animationView.setAnimation(R.raw.spinner_error)
                        binding.animationView.repeatCount = 0
                        binding.animationView.playAnimation()
                        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.INVISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.VISIBLE
                                binding.txtPatente.isEnabled = true
                                binding.ddlDivision.isClickable = true
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                binding.btnValidarPatente.visibility = View.VISIBLE
                            }

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                    }
                } else {
                    binding.btnValidarPatente.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom_red)
                    checkListIngresoViewModel.txtPatenteError.postValue("La patente no existe")
                    checkListIngresoViewModel.seValidoPatente.postValue(false)
                    binding.animationView.setAnimation(R.raw.spinner_error)
                    binding.animationView.repeatCount = 0
                    binding.animationView.playAnimation()
                    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            binding.btnValidarPatente.visibility = View.INVISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            binding.btnValidarPatente.visibility = View.VISIBLE
                            binding.txtPatente.isEnabled = true
                            binding.ddlDivision.isClickable = true
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            binding.btnValidarPatente.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                }
            })

        checkListIngresoViewModel.getValidarPatenteError().observe(viewLifecycleOwner, Observer {
            binding.ddlLocal.dismissDropDown()
            binding.ddlDivision.dismissDropDown()
            checkListIngresoViewModel.txtPatenteError.postValue("Ocurrio un error al validar la patente")
            binding.animationView.pauseAnimation()
            binding.txtPatente.isEnabled = true
            binding.btnValidarPatente.visibility = View.VISIBLE
            binding.animationView.visibility = View.INVISIBLE
        })

        //carga de divisiones
        checkListIngresoViewModel.getGetDivisionesResult()
            .observe(viewLifecycleOwner, Observer { divisions ->
                dismissLoader()
                if (divisions.isSuccess) {
                    listDivisiones = divisions.data.toMutableList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        listDivisiones!!
                    )
                    binding.ddlDivision.setAdapter(adapter)
                    if (!isLoadLastregister) {
                        binding.txtPatente.setText(checkListTest!!.vehicleId)
                        binding.ddlDivision.setText(checkListTest!!.divisionName, false)
                        loadLocal(checkListTest!!.divisionId)
                    }
                }
            })

        checkListIngresoViewModel.getGetDivisionesError().observe(viewLifecycleOwner, Observer {
            dismissLoader()
            Snackbar.make(requireView(), "Error de red. Timeout", Snackbar.LENGTH_INDEFINITE)
                .setAction("Reintentar") { loadDivisiones() }.show()
        })

        //carga de locales
        checkListIngresoViewModel.getGetLocalesResult()
            .observe(viewLifecycleOwner, Observer { locals ->
                dismissLoader()
                listLocales!!.clear()
                if (locals.isSuccess) {
                    listLocales = locals.data.toMutableList()
                    if (listLocales!!.size > 0) {
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            listLocales!!
                        )
                        binding.ddlLocal.setAdapter(adapter)
                    } else {
                        listLocales!!.add(Local("SIN_LOCAL", "SIN LOCAL"))
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            listLocales!!
                        )
                        binding.ddlLocal.setAdapter(adapter)
                    }
                    if (!isLoadLastregister) {
                        binding.ddlLocal.setText(checkListTest!!.localName, false)
                        isLoadLastregister = true
                    }
                }
            })

        checkListIngresoViewModel.getGetLocalesError().observe(viewLifecycleOwner, Observer {
            dismissLoader()
            Snackbar.make(requireView(), "Error de red. Timeout", Snackbar.LENGTH_INDEFINITE)
                .setAction("Reintentar") { loadDivisiones() }.show()
        })

        checkListIngresoViewModel.getCompanyResult.observe(
            viewLifecycleOwner,
            Observer { workerAngloApiResponseAnglo ->
                if (workerAngloApiResponseAnglo.isSuccess) {
                    binding.txtEmpresa.text = workerAngloApiResponseAnglo.data.companiaNombre
                }
            })

        checkListIngresoViewModel.seValidoPatente.postValue(false)
        checkListIngresoViewModel.txtPatenteError.observe(
            viewLifecycleOwner,
            Observer { it: String? -> binding.txtPatenteError.error = it })
        checkListIngresoViewModel.ddlDivisionError.observe(
            viewLifecycleOwner,
            Observer { it: String? -> binding.ddlDivision.error = it })
        checkListIngresoViewModel.ddlLocalError.observe(
            viewLifecycleOwner,
            Observer { it: String? -> binding.ddlLocal.error = it })

        //validacion conductor
        checkListIngresoViewModel.workerByDivisionObservableResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                if (result.isSuccess) {
                    if (result.isSuccess) {
                        if (result.data.autor && result.data.validated) {
                            guardar(true)
                            setOSTCheckList(context, result.data.ost)
                            setCompanyCheckList(context, result.data.companiaId)
                            findNavController().navigate(
                                R.id.checkListTestFragment, bundleOf(
                                    "TipoCheckList" to TipoCheckList,
                                    "NomCheckList" to NomCheckList, "idChecklist" to idChecklist
                                )
                            )
                        } else if (result.data.autor && !result.data.validated) {
                            showUnaccredited()
                        } else {
                            showError()
                        }
                    } else {
                        showError()
                    }
                }
                ProgressLoadingJIGB.finishLoadingJIGB(context)
            })

        checkListIngresoViewModel.workerByDivisionObservableError.observe(
            viewLifecycleOwner,
            Observer {
                ProgressLoadingJIGB.finishLoadingJIGB(context)
                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar") { btnSiguiente() }.show()
            })
    }

    fun btnValidarPatente() {
            if (binding.txtPatente.text!!.isNotEmpty()) {
                binding.btnValidarPatente.isEnabled = true
                binding.btnValidarPatente.visibility = View.INVISIBLE
                binding.animationView.setAnimation(R.raw.loader_rest)
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.repeatCount = ValueAnimator.INFINITE
                binding.animationView.playAnimation()
                binding.txtPatente.isEnabled = false
                binding.ddlDivision.dismissDropDown()
                binding.ddlLocal.dismissDropDown()

            if (idCompany == Companies.KRS.valor) {
                checkListIngresoViewModel.validarPatenteKrs()
            } else {
                checkListIngresoViewModel.validarPatente()
            }
        } else {
                binding.txtPatenteError.error = "Ingrese patente"
        }
    }

    private fun guardar(isDriver: Boolean) {
        val checkListTest = CheckListTest()
        checkListTest.workerId = getUsuarioId(context)
        checkListTest.fechaSubmit = wCDate
        checkListTest.horaSubmit = time
        checkListTest.tipoTest = TipoCheckList
        checkListTest.estadoInterno = 0
        checkListTest.divisionId = "NNNN"
        checkListTest.divisionName = "SIN_DIVISION"
        checkListTest.localId = "SIN_LOCAL"
        checkListTest.localName = "SIN_LOCAL"
        checkListTest.vehicleId = "SIN_PATENTE"
        checkListTest.companyId = SharedUtils.getIdCompany(context)
        if (TipoCheckList == "COV") {
            checkListTest.divisionName = "ENCUESTA DIARIA"
            checkListTest.localId = ""
            checkListTest.localName = ""
            checkListTest.vehicleId = checkListTest.workerId
        }
        if (TipoCheckList == "DDS") {
            checkListTest.divisionName = "QUELLAVECO"
            checkListTest.divisionId = "QV"
            checkListTest.localId = ""
            checkListTest.localName = ""
            checkListTest.vehicleId = checkListTest.workerId
        }
        if (isDriver) {
            checkListTest.vehicleId = binding.txtPatente.text.toString().toUpperCase()
            checkListTest.divisionId = checkListIngresoViewModel.ddlDivisionIdSelected.value
            checkListTest.divisionName = binding.ddlDivision.text.toString()
            checkListTest.localId = checkListIngresoViewModel.ddlLocalIdSelected.value
            checkListTest.localName = binding.ddlLocal.text.toString()
        }
        App.db.checkListDao().insertCheckListTest(checkListTest)
    }

    fun btnSiguiente() {
        if (checkListIngresoViewModel.validarDatos()) {
            ProgressLoadingJIGB.startLoadingJIGB(
                context,
                R.raw.loaddinglottie,
                "Cargando...",
                0,
                500,
                200
            )
            if (idCompany == Companies.KRS.valor) {
                checkListIngresoViewModel.validarConductorKrs(
                    getUsuarioId(context),
                    checkListIngresoViewModel.ddlDivisionIdSelected.value
                )
            } else {
                checkListIngresoViewModel.validarConductor(
                    getUsuarioId(context),
                    checkListIngresoViewModel.ddlDivisionIdSelected.value
                )
            }
        }
    }

    private fun loadLocal(division: String?) {
        if (!isOnline(requireContext())) {
            if (view != null) {
                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar") { v: View? -> loadLocal(division) }.show()
            }
        } else {
            showLoader()
            if (idCompany == Companies.KRS.valor) {
                checkListIngresoViewModel.getLocalesKrs(division)
            } else {
                checkListIngresoViewModel.getLocales(division)
            }
        }
    }

    private fun loadEmpresas() {
        if (!isOnline(requireContext())) {
            if (view != null) {
                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                    .setAction("Reintentar") { view: View? -> loadEmpresas() }.show()
            }
        } else {
            if (idCompany == Companies.KRS.valor) {
                checkListIngresoViewModel.getWorkerCompanyKrs(getUsuarioId(context))
            } else {
                checkListIngresoViewModel.getWorkerCompany(getUsuarioId(context))
            }
        }
    }

    private fun loadDivisiones() {
        if (!isOnline(requireContext())) {
            if (view != null) {
                Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar") { loadDivisiones() }.show()
            }
        } else {
            showLoader()
            if (idCompany == Companies.KRS.valor) {
                checkListIngresoViewModel.getDivisionsKrs()
            } else {
                checkListIngresoViewModel.getDivisions()
            }
        }
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            "Cargando...",
            0,
            500,
            200
        )
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    private fun showError() {
        MaterialDialog.Builder(requireContext())
            .title("Error")
            .content("Usted no es un usuario conductor.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    private fun showUnaccredited() {
        MaterialDialog.Builder(requireContext())
            .title("Error")
            .content("Su usuario no se registra como acreditado para esta Division.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    override fun onBackPressed(): Boolean {
        if (TipoCheckList != "TFS") {
            NomCheckList = "Vehículos"
        }
        findNavController().navigate(
            R.id.historicoCheckListFragment, bundleOf(
                HistoricoCheckListFragment.CHECKLIST_TYPE to TipoCheckList,
                HistoricoCheckListFragment.CHECKLIST_NAME to NomCheckList,
                HistoricoCheckListFragment.CHECKLIST_ID to idChecklist
            )
        )
        return false
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"

        @JvmStatic
        fun newInstance(
            param1: String?,
            param2: String?,
            param3: String?
        ): CheckListIngresoFragment {
            val fragment = CheckListIngresoFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            args.putString(ARG_PARAM3, param3)
            fragment.arguments = args
            return fragment
        }
    }
}