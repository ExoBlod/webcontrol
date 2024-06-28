package com.webcontrol.android.ui.credential

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getNiceDate
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class CredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialBinding
    lateinit var worker: WorkerAnglo

    companion object {
        private const val WORKER = "WORKER"

        fun newInstance(worker: WorkerAnglo): CredentialFragment {
            val args = Bundle()
            args.putSerializable(WORKER, worker)
            val fragment = CredentialFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(aaa: String?): CredentialFragment {
            val fragment = CredentialFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        SharedUtils.showLoader(context, "Cargando...")
        val api: RestInterfaceAnglo = RestClient.buildAnglo()
        val call = api.getWorker(
                object : HashMap<String, String>() {
                    init {
                            put("WorkerId", SharedUtils.getUsuarioId(context))
                    }
                }
        )
        call.enqueue(object : Callback<ApiResponseAnglo<WorkerAnglo>> {
            override fun onResponse(call: Call<ApiResponseAnglo<WorkerAnglo>>, response: Response<ApiResponseAnglo<WorkerAnglo>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess && response.body()!!.data != null) {
                        worker = response.body()!!.data
                        setUIElements()
                    }else{
                        setUISinCredencial()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<WorkerAnglo>>, t: Throwable) {
                t.printStackTrace()
                Snackbar.make(view!!, "Error de red", Snackbar.LENGTH_LONG).setAction("Reintentar") { loadData() }.show()
                SharedUtils.dismissLoader(context)
            }
        })
    }

    private fun setUIElements() {
        if (worker.credencial != null) {
            if (worker.companiaId.equals("77905330K")) {
                setUIWorkerPropio()
            }
            binding.lblName.text = "${worker.nombre} ${worker.apellidos}"
            binding.lblId.text = SharedUtils.FormatRut(worker.id)
            binding.lblEmpresa.text = worker.companiaNombre
            binding.lblRutEmpresa.text = worker.companiaId
            binding.lblFecha.text = getNiceDate(worker.credentialEndDate)
            binding.imgQr.setImageBitmap(generateQR(formatQRContent(worker)))
        } else {
            setUISinCredencial()
        }
        SharedUtils.dismissLoader(context)
    }

    private fun setUISinCredencial(){
        binding.lblName.text = "El trabajador con ID ${SharedUtils.getUsuarioId(context)} no cuenta con credencial activa"
        binding.textFecha.visibility = GONE
    }

    private fun setUIWorkerPropio() {
        binding.viewHeader.background = resources.getDrawable(R.color.white)
        binding.viewBody!!.background = resources.getDrawable(R.color.colorCredentialAnglo)
        binding.lblTitle!!.setTextColor(resources.getColor(R.color.defaultContentColor))
        binding.lblTipoworker!!.setTextColor(resources.getColor(R.color.defaultContentColor))
        binding.lblTipoworker!!.text = "TRABAJADOR PROPIO"
        binding.lblName.setTextColor(resources.getColor(R.color.white))
        binding.lblId.setTextColor(resources.getColor(R.color.white))
        binding.lblEmpresa.setTextColor(resources.getColor(R.color.contentDividerLine))
        binding.lblRutEmpresa.setTextColor(resources.getColor(R.color.contentDividerLine))
        binding.textFecha.setTextColor(resources.getColor(R.color.contentDividerLine))
        binding.lblFecha.setTextColor(resources.getColor(R.color.contentDividerLine))

    }

    private fun formatQRContent(worker: WorkerAnglo): String {
        return "ANGLOAMERICAN" +
                "\n" +
                "\n" +
                "\n" +
                "Nombre: ${worker.nombre} ${worker.apellidos}" +
                "\n" +
                "Rut: ${worker.id}" +
                "\n" +
                "\n" +
                "Empresa: ${worker.companiaNombre}" +
                "\n" +
                "Rut Empresa: ${worker.companiaId}"

    }

    private fun generateQR(source: String, height: Int = 512, width: Int = 512): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
    }
}