package com.webcontrol.android.ui.owndoc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mindorks.paracamera.Camera
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.dto.InsertDocDTO
import com.webcontrol.android.data.network.dto.TypeOwnDocs
import com.webcontrol.android.databinding.FragmentUpdateDocBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_datos_iniciales_datospersonales.*
import kotlinx.android.synthetic.main.fragment_update_doc.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val SELECT_ACTIVITY = 50

@AndroidEntryPoint
class UpdateDocFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentUpdateDocBinding
    private var param1: String? = null
    var camera: Camera? = null
    var isPhotoFront: Boolean = true
    var byteArrayFront: ByteArray? = null
    var byteArrayBack: ByteArray? = null
    var byteArrayFile: ByteArray? = null
    var idDocSelected = -1
    var typeFileSelected = false

    private lateinit var documentDialogBuilder: MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.upload_file)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateDocBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        ShowLoader()
        val api = RestClient.buildWebControlDocs()
        val call = api.getTypeOwnDocs()
        call.enqueue(object : retrofit2.Callback<List<TypeOwnDocs>> {
            override fun onFailure(call: Call<List<TypeOwnDocs>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<List<TypeOwnDocs>>, response: Response<List<TypeOwnDocs>>) {
                if (response.isSuccessful) {
                    val listTypeDocDTO = response.body()!!

                    val listTypeDoc= getListofDTOs(listTypeDocDTO)
                    val adapterListTypeDoc: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listTypeDoc)

                    sp_type_doc.setAdapter(adapterListTypeDoc)
                    sp_type_doc.setOnItemClickListener { parent, view, position, id ->

                        val item = listTypeDocDTO.find { typeDocsDTO ->
                            typeDocsDTO.NOMBRE == listTypeDoc[position]
                        }!!

                        idDocSelected = item.ID_DOC

                        val showPhoto = item.LISTA_FORMATOS.find {
                            it.EXTENSION == ".PNG" || it.EXTENSION == ".JPG"
                        } != null

                        if (showPhoto){
                            tilTakePic.visibility = View.VISIBLE
                            txtTakePic.visibility = View.VISIBLE
                            //TODO actualización imPhotoPrimary.visibility = View.VISIBLE

                            if (item.AMBAS_CARAS == "SI"){
                                tilTakePic2.visibility = View.VISIBLE
                                txtTakePic2.visibility = View.VISIBLE
                                //TODO actualización imPhotoPrimary2.visibility = View.VISIBLE
                            } else {
                                tilTakePic2.visibility = View.GONE
                                txtTakePic2.visibility = View.GONE
                                //TODO actualización imPhotoPrimary2.visibility = View.GONE
                            }
                        }else   {
                            tilTakePic.visibility = View.GONE
                            txtTakePic.visibility = View.GONE
                            //TODO actualización imPhotoPrimary.visibility = View.GONE
                        }
                    }
                }
                DismissLoader()
            }

        })

        btn_process.setOnClickListener {
            if(idDocSelected == -1)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_a_file),
                    Toast.LENGTH_SHORT
                ).show()
            else if(byteArrayFront == null && byteArrayFile == null)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_upload_a_file),
                        Toast.LENGTH_SHORT
                    ).show()
            else
                documentDialogBuilder
                    .setTitle("Confirmar")
                    .setMessage(getString(R.string.are_you_sure_you_want_to_upload_file))
                    .setNegativeButton("CANCELAR"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("ACEPTAR") { _, _ ->
                        Toast.makeText(context, getString(R.string.sending_data_to_server), Toast.LENGTH_SHORT).show()
                        ShowLoader()
                        val uuid = UUID.randomUUID().toString()
                        val insertDocDTO = InsertDocDTO(uuid,idDocSelected,SharedUtils.getUsuarioId(context))
                        val call2 = api.insertOwnDocs(insertDocDTO)
                        call2.enqueue(object : retrofit2.Callback<Any> {
                            override fun onFailure(call: Call<Any>, t: Throwable) {
                                SharedUtils.showToast(requireContext(), getString(R.string.error_sending_information))
                                DismissLoader()
                            }

                            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                if (response.isSuccessful) {
                                    SharedUtils.showToast(requireContext(), getString(R.string.sending_files))
                                    if(typeFileSelected){
                                        val fileFront = MultipartBody.Part.createFormData(
                                            uuid, "${etUpdateFile.text}",
                                            byteArrayFile!!.toRequestBody("application/pdf".toMediaType(), 0)
                                        )
                                        sendFile(uuid,1,fileFront,requireContext())
                                    } else {
                                        val fileFront = MultipartBody.Part.createFormData(
                                            uuid, "${etTakePic.text}",
                                            byteArrayFront!!.toRequestBody("image/jpeg".toMediaType(), 0)
                                        )
                                        sendFile(uuid,1,fileFront,requireContext())
                                    }
                                    onBackPressed()
                                }
                                DismissLoader()
                            }

                        })
                    }
                    .show()
        }

        etTakePic.setOnClickListener {
            isPhotoFront = true
            takePic()
        }

        etTakePic2.setOnClickListener {
            isPhotoFront = false
            takePic()
        }

        etUpdateFile.setOnClickListener{
            ImageController.selectFileFromGallery(this, SELECT_ACTIVITY)
        }

        imPhotoPrimary.setOnClickListener {
            byteArrayFront?.let { it1 -> PhotoDialogFragment.newInstance(it1,etTakePic.text.toString()).show(parentFragmentManager,PhotoDialogFragment.TAG) }
        }

        imPhotoPrimary2.setOnClickListener {
            byteArrayBack?.let { it1 -> PhotoDialogFragment.newInstance(it1,etTakePic2.text.toString()).show(parentFragmentManager,PhotoDialogFragment.TAG) }
        }

        imPhotoPrimary3.setOnClickListener {
            byteArrayFile?.let { it1 ->
                if( isImage(etUpdateFile.text.toString()))
                    PhotoDialogFragment.newInstance(it1,etUpdateFile.text.toString()).show(parentFragmentManager,PhotoDialogFragment.TAG)
                else
                    Toast.makeText(requireContext(), "Solo se visualiza imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendFile(
        idFile: String,
        cara: Int,
        image: MultipartBody.Part,
        context: Context
    ){
        val api = RestClient.buildWebControlDocs()
        val call = api.insertFileOwnDocs(idFile,cara,image)
        val filename = if(etTakePic!=null) etTakePic.text.toString() else ""
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                SharedUtils.showToast(
                    context,
                    getString(R.string.error_sending_information)
                )
                DismissLoader()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {
                if (response.isSuccessful) {
                    SharedUtils.showToast(
                        context,
                        context.getString(R.string.file_sent_successfully)
                    )
                    if (byteArrayBack != null){
                        val fileBack = MultipartBody.Part.createFormData(
                            idFile,
                            filename,
                            byteArrayBack!!.toRequestBody("image/jpeg".toMediaType(),
                                0)
                        )
                        byteArrayFront = null
                        byteArrayBack = null
                        sendFile(idFile, 2, fileBack, context)
                    }
                }
            }
        })
    }

    private fun isImage(nameFile: String): Boolean =
        nameFile.substring(nameFile.length-3).lowercase()!="pdf"


    override fun onDestroy() {
        super.onDestroy()
        if(camera != null)
            camera!!.deleteImage()
    }

    private fun takePic(){
        try {
            camera = Camera.Builder()
                .resetToCorrectOrientation(true)
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)
                .build(this)

            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        try {
                            camera!!.takePicture()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if (response!!.isPermanentlyDenied){
                            MaterialDialog.Builder(requireContext())
                                .title(R.string.app_name)
                                .content("Active el permiso de cámara para tomar la foto.")
                                .positiveText(android.R.string.ok)
                                .onPositive { dialog, which ->
                                    dialog.dismiss()
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:${requireContext().packageName}"))
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                                .negativeText(android.R.string.cancel)
                                .onNegative { dialog: MaterialDialog, which: DialogAction? ->
                                    dialog.dismiss()
                                }
                                .autoDismiss(false)
                                .cancelable(false)
                                .show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
        } catch (ex: Exception) {
            SharedUtils.showToast(requireContext(), ex.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera!!.cameraBitmap
                if (bitmap != null) {
                    typeFileSelected = false
                    etUpdateFile.isEnabled = false
                    when(isPhotoFront){
                        true -> {
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                            val image = stream.toByteArray()
                            etTakePic.setText( "front_" + (System.currentTimeMillis()/1000).toInt() + ".jpg" )
                            byteArrayFront = image
                        }
                        false -> {
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                            val image = stream.toByteArray()
                            etTakePic2.setText( "back_" + (System.currentTimeMillis()/1000).toInt() + ".jpg" )
                            byteArrayBack = image
                        }
                    }
                } else {
                    SharedUtils.showToast(requireContext(), "Foto no tomada Intentelo nuevamente!")
                }
            } else if (requestCode == SELECT_ACTIVITY){
                typeFileSelected = true
                etTakePic.isEnabled = false
                etTakePic2.isEnabled = false

                if(data == null && byteArrayFile==null) {
                    typeFileSelected = false
                    etTakePic.isEnabled = true
                    etTakePic2.isEnabled = true
                    SharedUtils.showToast(requireContext(), getString(R.string.no_file_selected))
                    return
                }
                val uri = data!!.data!!
                val iStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
                var name = ""
                uri.let {
                    requireActivity().contentResolver.query(it,null,null,null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    name = cursor.getString(nameIndex)
                }
                etUpdateFile.setText(name)
                byteArrayFile = getBytes(iStream!!)
            }
        } catch (ex: Exception) {
            SharedUtils.showToast(requireContext(), getString(R.string.unrecognized_file))
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
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

    fun getListofDTOs(list:List<TypeOwnDocs>):List<String>{
        val listString = mutableListOf<String>()
        list.forEach{ dto->
            listString.add(dto.NOMBRE)
        }
        return listString
    }

    companion object {

        fun newInstance(param1: String) =
            UpdateDocFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.navigateTo(
            OwnDocFragment.newInstance(
                getString(R.string.own_document)
            ), getString(R.string.own_document), 0
        )
        return false
    }
}