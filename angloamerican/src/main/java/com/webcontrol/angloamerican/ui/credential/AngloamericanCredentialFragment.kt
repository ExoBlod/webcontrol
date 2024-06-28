package com.webcontrol.angloamerican.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CredentialBack
import com.webcontrol.angloamerican.data.network.response.CredentialBackVehicle
import com.webcontrol.angloamerican.data.network.response.CredentialFront
import com.webcontrol.angloamerican.data.network.response.CredentialListCourses
import com.webcontrol.angloamerican.data.network.response.WorkerCredential
import com.webcontrol.angloamerican.databinding.FragmentAngloamericanCredentialBinding
import com.webcontrol.angloamerican.databinding.PopupInformationBinding
import com.webcontrol.angloamerican.ui.credential.adapter.AuthorizedAreasAdapter
import com.webcontrol.angloamerican.ui.credential.adapter.CredentialBackAdapter
import com.webcontrol.angloamerican.ui.credential.adapter.CredentialListCoursesAdapter
import com.webcontrol.angloamerican.ui.credential.adapter.CredentialVehicleAdapter
import com.webcontrol.angloamerican.ui.security.CredentialViewModel
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Success
import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_angloamerican_credential.includeBack
import kotlinx.android.synthetic.main.fragment_angloamerican_credential.includeFront
import kotlinx.android.synthetic.main.fragment_credential_back_anglo.view.cardFront
import kotlinx.android.synthetic.main.fragment_credential_back_anglo.view.cardFrontZones
import kotlinx.android.synthetic.main.fragment_credential_back_anglo.view.rvAuthZone
import kotlinx.android.synthetic.main.fragment_credential_back_anglo.view.titulo
import kotlinx.android.synthetic.main.fragment_credential_front_anglo.view.txtZona
import javax.inject.Inject

@AndroidEntryPoint
class AngloamericanCredentialFragment : Fragment() {
    @Inject
    lateinit var localStorage: LocalStorage
    private lateinit var binding: FragmentAngloamericanCredentialBinding
    private lateinit var bindingDialog: PopupInformationBinding
    private val credentialViewModel by activityViewModels<CredentialViewModel>()
    private var credentialListCourses: ArrayList<CredentialListCourses> = arrayListOf()
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true

    private var authDivision = ""
    private var authZones = ""

    private var foundLT:Boolean = false;
    private var foundLB:Boolean = false;
    private var foundES:Boolean = false;

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAngloamericanCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density

        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet

        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet

        with(binding) {
            includeFront.frontAnglo.cameraDistance = 8000 * scale
            includeBack.backAnglo.cameraDistance = 8000 * scale

            btnFlipAnglo.setOnClickListener {
                btnFlipAnglo.hide()
                flipCard()
                handler.postDelayed({
                    btnFlipAnglo.show()
                }, 1000)
            }

            includeFront.ivQr.setImageBitmap(generateQR(localStorage["USER_ID", ""] ?: ""))

            includeBack.ivMoreInfo.setOnClickListener {

                val dialogBinding = layoutInflater.inflate(R.layout.popup_information, null)
                val myDialog = Dialog(requireContext())
                val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
                with(myDialog) {
                    setContentView(dialogBinding)
                    setCancelable(true)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                    yesBtn.setOnClickListener { dismiss() }
                }
            }

            includeBack.ivInfoCourses.setOnClickListener {
                val dialogCoursesBinding =
                    layoutInflater.inflate(R.layout.popup_courses_information, null)
                val btnOk = dialogCoursesBinding.findViewById<Button>(R.id.btnOKCourses)

                val popupWindow = PopupWindow(
                    dialogCoursesBinding,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                popupWindow.isFocusable = true
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val credentialListCourses: ArrayList<CredentialListCourses> = credentialListCourses
                val recyclerView =
                    dialogCoursesBinding.findViewById<RecyclerView>(R.id.rcvCredentialCourses)
                val adapter = CredentialListCoursesAdapter(credentialListCourses)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                btnOk.setOnClickListener {
                    popupWindow.dismiss()
                }

                popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
            }
        }

        loadData()

    }

    private fun flipCard() {
        with(binding) {
            includeBack.backAnglo.alpha = 1f
            if (isValid) {
                isFront = if (isFront) {
                    frontAnim.setTarget(includeFront.frontAnglo)
                    backAnim.setTarget(includeBack.backAnglo)
                    frontAnim.start()
                    backAnim.start()
                    false
                } else {
                    frontAnim.setTarget(includeBack.backAnglo)
                    backAnim.setTarget(includeFront.frontAnglo)
                    backAnim.start()
                    frontAnim.start()
                    true
                }
            }
        }

    }

    private fun loadData() {
        credentialViewModel.getCredential(localStorage["USER_ID", ""] ?: "")
        credentialViewModel.getAuthorizedDivisions(localStorage["USER_ID", ""] ?: "")
        credentialViewModel.getAuthorizedAreas(localStorage["USER_ID", ""] ?: "")
        credentialViewModel.credential.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    // TODO: add loader
                }

                is Success -> {
                    if (result.data != null) {
                        isValid = true
                        val workerC = result.data

                        initContentCredentialFront(result.data.credentialFront)
                        if (result.data.credentialBack.isNotEmpty()) {
                            foundDivisions(result.data);
                            initContentCredentialBackVehicle(workerC.credentialBackVehicle)
                        } else {
                            binding.btnFlipAnglo.hide()
                        }
                        credentialListCourses = result.data.credentialListCourses

                    } else {
                        isValid = false
                        setUISinCredential()
                        return@observe
                    }

                }

                is Error -> {
                    isValid = false
                    Toast.makeText(context, "Error obteniendo credencial", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun initContentCredentialFront(front: CredentialFront) {
        with(binding) {

            includeFront.imgProfileAnglo.visibility = View.VISIBLE

            if (front.photo?.isNotEmpty() == true)
                loadImg(front.photo!!)
            else
                includeFront.imgProfileAnglo.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)

            includeFront.txtCompany.text = front.company
            includeFront.txtDNI.text =
                SharedUtils.FormatRut(front.rut)

            includeFront.txtName.text = front.firstName
            includeFront.txtApellido.text = front.lastName
            includeFront.txtJob.text = front.rol
            includeFront.txtArea.text = front.area

            includeFront.txtAuthorizationDrive.text =
                front.autorizathe
            includeFront.txtExpeditiondate.text =
                SharedUtils.getNiceDate(front.dateDriverLic)
            includeFront.tvClasses.text =
                front.municipal.ifEmpty { "NA" }

            includeFront.txtZona.text =
                front.accessZones.replace(",", "\t\t\t\t")

            authDivision = front.div;
        }
    }

    private fun initContentCredentialBack(credentialBack: ArrayList<CredentialBack>) {
        with(binding) {
            includeBack.tvRestrictions.text =
                if (credentialBack[0].retrictions == "") getString(R.string.empty) else credentialBack[0].retrictions
            val recycleViewBack = includeBack.rvBackCredential
            recycleViewBack.layoutManager = LinearLayoutManager(requireContext())
            recycleViewBack.adapter = CredentialBackAdapter(credentialBack);
        }
    }

    private fun initContentCredentialBackVehicle(credentialBackVehicle: ArrayList<CredentialBackVehicle>) {
        with(binding) {
            val recycleViewVehicle = includeBack.rvVehicleList
            recycleViewVehicle.layoutManager = LinearLayoutManager(requireContext())
            recycleViewVehicle.adapter = CredentialVehicleAdapter(credentialBackVehicle)
        }
    }

    private fun setUISinCredential() {
        with(binding) {
            includeFront.titulo.text =
                getString(R.string.without_credential, SharedUtils.getUsuarioId(context))
            includeFront.ivQr.visibility = View.GONE
            binding.btnFlipAnglo.hide()
        }

    }

    private fun loadImg(photo: String) {
        val image: ByteArray?
        val bitmap: Bitmap?
        try {
            image = Base64.decode(photo, 0)
            val options = BitmapFactory.Options()
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.size, options)
            Glide.with(this)
                .load(bitmap)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .fitCenter()
                .circleCrop()
                .into(binding.includeFront.imgProfileAnglo)
        } catch (e: Exception) {
            SharedUtils.showToast(requireContext(), e.message)
        }
    }

    private fun generateQR(source: String, height: Int = 1024, width: Int = 1024): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
    }

    private fun foundDivisions(resultCB: WorkerCredential){
        credentialViewModel.authDivisions.observe(viewLifecycleOwner) {result ->
            when(result){
                is Loading -> {
                    // TODO: add loader
                }

                is Success -> {
                    if (!result.data.isNullOrEmpty()){
                        result.data.forEach { div ->
                            if (div.division == "LB"){
                                foundLB = true;
                            }
                            if (div.division == "LT"){
                                foundLT = true;
                            }
                            if (div.division == "ES"){
                                foundES = true;
                            }
                        }

                        showOrHideExplorK(resultCB = resultCB)
                        showOrHideAuthZonesBack()
                    }else{
                        foundLT = false;
                        foundLB = false;
                        foundES = false;
                        includeBack.cardFront.visibility = View.GONE
                        includeBack.titulo.visibility = View.GONE
                        includeBack.cardFrontZones.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showOrHideExplorK(resultCB: WorkerCredential){
        if (foundLB == true || foundLT == true){
            initContentCredentialBack(resultCB.credentialBack)
        }else{
            includeBack.cardFront.visibility = View.GONE
            includeBack.titulo.visibility = View.GONE
        }
    }
    private fun showOrHideAuthZonesBack() {

        if (foundES == true) {
            credentialViewModel.authAreas.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is Loading -> {
                        // TODO: add loader
                    }

                    is Success -> {
                        if (result.data != null) {
                            val listAuthAreas = result.data
                            val recyclerViewAuthorizedAreas = includeBack.rvAuthZone
                            recyclerViewAuthorizedAreas.layoutManager =
                                LinearLayoutManager(requireContext())
                            recyclerViewAuthorizedAreas.adapter =
                                AuthorizedAreasAdapter(listAuthAreas)

                        }
                    }
                }
            }
        }else{
            includeBack.cardFrontZones.visibility = View.GONE
        }
    }
}


