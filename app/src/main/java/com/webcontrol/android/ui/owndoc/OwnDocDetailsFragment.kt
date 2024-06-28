package com.webcontrol.android.ui.owndoc

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.network.dto.OwnDocs
import com.webcontrol.android.databinding.FragmentOwnDocDetailBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.correctFormatDate
import com.webcontrol.android.util.correctState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_own_doc_detail.*
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.net.URL
import java.net.URLConnection

private const val ARG_PARAM_TITLE = "param1"
private const val ARG_PARAM_ID_DOC = "param2"

@AndroidEntryPoint
class OwnDocDetailsFragment : Fragment(), IOnBackPressed {
    private var txtTitle: String? = null
    private var idDoc: String? = null
    private lateinit var binding: FragmentOwnDocDetailBinding
    private lateinit var txtTypeDoc: TextView
    private lateinit var txtUpdateDate: TextView
    private lateinit var txtUpdateHour: TextView
    private lateinit var txtChangeDate: TextView
    private lateinit var txtChangeHour: TextView
    private lateinit var txtExpirationDate: TextView
    private lateinit var txtExpirationHour: TextView
    private lateinit var txtRut: TextView
    private lateinit var txtUserCert: TextView
    private lateinit var txtState: TextView
    private lateinit var clEmpty: ConstraintLayout
    private lateinit var clFull: ConstraintLayout
    private lateinit var ivDocOwn: ImageView

    var ownDocsById: OwnDocs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        txtTitle = requireArguments().getString(ARG_PARAM_TITLE)
        idDoc = requireArguments().getString(ARG_PARAM_ID_DOC)

        activity?.title = getString(R.string.upload_file)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentOwnDocDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTypeDoc = view.findViewById(R.id.txtTypeDoc)
        txtUpdateDate = view.findViewById(R.id.txtUpdateDate)
        txtUpdateHour = view.findViewById(R.id.txtUpdateHour)
        txtChangeDate = view.findViewById(R.id.txtChangeDate)
        txtChangeHour = view.findViewById(R.id.txtChangeHour)
        txtExpirationDate = view.findViewById(R.id.txtExpirationDate)
        txtExpirationHour = view.findViewById(R.id.txtExpirationHour)
        txtState = view.findViewById(R.id.txtState)
        clEmpty = view.findViewById(R.id.emptyDetail)
        clFull = view.findViewById(R.id.fullDetail)
        ivDocOwn = view.findViewById(R.id.ivDocOwn)


        ShowLoader()
        val api = RestClient.buildWebControlDocs()
        val call = api.getOwnDocsById(idDoc!!)
        call.enqueue(object : retrofit2.Callback<List<OwnDocs>> {
            override fun onFailure(call: Call<List<OwnDocs>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<List<OwnDocs>>, response: Response<List<OwnDocs>>) {
                if (response.isSuccessful) {
                    if(response.body()!!.isNotEmpty()){
                        clEmpty.visibility = View.GONE
                        clFull.visibility = View.VISIBLE
                        ownDocsById = response.body()!![0]

                        txtTypeDoc.text = ownDocsById!!.NOMBRE
                        txtUpdateDate.text = ownDocsById!!.FECHASUBE.correctFormatDate()
                        txtUpdateHour.text = ownDocsById!!.HORASUBE
                        txtChangeDate.text = ownDocsById!!.FECHA_MOD.correctFormatDate()
                        txtChangeHour.text = ownDocsById!!.HORA_MOD
                        txtExpirationDate.text = if (ownDocsById!!.CERTFECHA==null) "Pendiente" else ownDocsById!!.CERTFECHA.correctFormatDate()
                        txtExpirationHour.text = if (ownDocsById!!.CERTHORA==null) "Pendiente" else ownDocsById!!.CERTHORA
                        txtState.text = ownDocsById!!.VALIDADO.correctState()

                        changeColorText(ownDocsById!!)
                    } else{
                        clEmpty.visibility = View.VISIBLE
                        clFull.visibility = View.GONE
                    }

                    DismissLoader()
                }
                SharedUtils.dismissLoader(requireContext())
            }
        })

        ivDocOwn.setOnClickListener {
            val thread = object : Thread() {
                override fun run() {
                    try {
                        val url = URL(ownDocsById!!.ARCHIVO)
                        val extensionFile = ownDocsById!!.NOMBRE_ARCHIVO.substring(ownDocsById!!.NOMBRE_ARCHIVO.length-3)
                        val mimetype = if(extensionFile == "pdf") "application/pdf" else "images/jpeg"

                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, ownDocsById!!.NOMBRE_ARCHIVO)
                            put(MediaStore.MediaColumns.MIME_TYPE, mimetype)
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }
                        val resolver = requireContext().contentResolver
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                            if(uri!=null){
                                val inputStream: InputStream = ByteArrayInputStream(url.readBytes())
                                inputStream.use { input ->
                                    resolver.openOutputStream(uri).use { output ->
                                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                                    }
                                }
                            }
                        } else {
                            val root: File = Environment.getExternalStorageDirectory()

                            val dir = File(root.absolutePath + "/" + Environment.DIRECTORY_DOWNLOADS)
                            if (dir.exists() === false) {
                                dir.mkdirs()
                            }

                            val url = URL(ownDocsById!!.ARCHIVO)

                            val file = File(dir, ownDocsById!!.NOMBRE_ARCHIVO)

                            val ucon: URLConnection = url.openConnection()
                            val inputStream: InputStream = ucon.getInputStream()
                            val bis = BufferedInputStream(inputStream)
                            var current = 0
                            val buffer = ByteArrayOutputStream()
                            val data = ByteArray(50)

                            while (bis.read(data, 0, data.size).also { current = it } != -1) {
                                buffer.write(data, 0, current)
                            }

                            val fos = FileOutputStream(file)
                            fos.write(buffer.toByteArray())
                            fos.flush()
                            fos.close()
                        }

                        activity!!.runOnUiThread {
                            SharedUtils.showToast(requireContext(),"Se guardo en descargas el archivo: ${ownDocsById!!.NOMBRE_ARCHIVO}")
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            thread.start()
        }
    }

    private fun changeColorText(ownDocsById: OwnDocs) {
        when (ownDocsById.VALIDADO) {
            StateUpdateDoc.APROBADO.state -> {
                txtStateDate.text = getString(R.string.txt_date_aprobation)
                txtStateHour.text = getString(R.string.txt_hour_aprobation)
                txtState.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        Color.GREEN.rgb
                    )
                )
                txtRejectReasons.visibility = View.INVISIBLE
            }
            StateUpdateDoc.EN_REVISION.state -> {

                txtStateDate.text = getString(R.string.txt_date_revision)
                txtStateHour.text = getString(R.string.txt_hour_revision)
                txtState.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        Color.YELLOW.rgb
                    )
                )
                txtRejectReasons.visibility = View.INVISIBLE
            }
            StateUpdateDoc.RECHAZADO.state -> {
                txtStateDate.text = getString(R.string.txt_date_reject)
                txtStateHour.text = getString(R.string.txt_hour_reject)
                txtState.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        Color.RED.rgb
                    )
                )
                val listRejectOwnDocs = mutableListOf<String>()
                ownDocsById.LISTA_RECHAZOS.forEach {
                    listRejectOwnDocs.add(it.TIPO_RECHAZO)
                }
                listRejectOwnDocs[0] = "-> "+listRejectOwnDocs[0]
                txtRejectReasons.text = listRejectOwnDocs.joinToString("\n-> ")
            }
        }
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

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.navigateTo(
            OwnDocFragment.newInstance(
                getString(R.string.own_document)
            ), getString(R.string.own_document), 0
        )
        return false
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OwnDocDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_TITLE, param1)
                    putString(ARG_PARAM_ID_DOC, param2)
                }
            }
    }
}