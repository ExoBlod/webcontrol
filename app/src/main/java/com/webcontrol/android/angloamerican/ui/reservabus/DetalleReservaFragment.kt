package com.webcontrol.android.angloamerican.ui.reservabus

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentDetalleReservaBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.angloamerican.utils.Utils
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class DetalleReservaFragment: Fragment() {
    private lateinit var binding: FragmentDetalleReservaBinding
    private val viewModel by viewModels<DetalleReservaViewModel>()

    lateinit var worker: WorkerAnglo

    private var reservaBus: ReservaBus2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reservaBus = requireArguments().getSerializable("item") as ReservaBus2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalleReservaBinding.inflate(inflater, container, false)
        //val view= inflater.inflate(R.layout.fragment_detalle_reserva, container, false)
        reservaBus!!.let{
            binding.lblHora.text = it.time
            binding.lblFecha.text = Utils.getNiceDate(it.date)
            binding.lblConductor1.text = if(it.driver1.isNullOrEmpty()) "No Asignado" else it.driver1
            binding.lblConductor2.text = if(it.driver2.isNullOrEmpty()) "No Asignado" else it.driver2
            binding.lblRutPasajero.text = it.workerId
            binding.lblOrigen.text = it.source
            binding.lblDestino.text = it.destiny
            binding.lblPatente.text = it.codeBus
            binding.lblAsiento.text = "Nro. Asiento: ${it.seat}"
            if (it.statusReserve == "SI") {
                binding.btnCancel.visibility = View.VISIBLE
                binding.txtCanceladoFyH.visibility = View.GONE
            } else {
                binding.btnCancel.visibility = View.GONE
                binding.imageQR.visibility = View.GONE
                binding.lbldetalle.text="Detalle Reserva Cancelada"
                binding.txtCanceladoFyH.text ="Cancelado:"+Utils.getFormatCredentialDate(it.fechaCancelada) +"  " +  it.horaCancelada
            }
        }
        binding.btnBack.setOnClickListener {
            clickBtnBack()
        }
        binding.btnCancel.setOnClickListener {
            clickBtnCancel()
        }
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCancelReserveBusState()
        reservaBus!!.let{
            if(it.statusReserve == "SI")
                binding.imageQR.setImageBitmap(generateQR(reservaBus!!.transacId!!, color = Color.BLACK))
        }
        loadDataWorker()
        setUserPhoto()
    }



    private fun loadDataWorker() {
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
            override fun onResponse(
                call: Call<ApiResponseAnglo<WorkerAnglo>>,
                response: Response<ApiResponseAnglo<WorkerAnglo>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess && response.body()!!.data != null) {
                        worker = response.body()!!.data
                        if(worker != null)
                            binding.lblNombrePasajero.text = "${worker.nombre} ${worker.apellidos}"

                        else
                            binding.lblNombrePasajero.text = SharedUtils.getUsuarioId(context)
                        SharedUtils.dismissLoader(context)
                    } else {
                        binding.lblNombrePasajero.text = SharedUtils.getUsuarioId(context)
                        SharedUtils.dismissLoader(context)
                    }
                }
            }



            override fun onFailure(call: Call<ApiResponseAnglo<WorkerAnglo>>, t: Throwable) {
                t.printStackTrace()
                Snackbar.make(view!!, "Error de red", Snackbar.LENGTH_LONG)
                    .setAction("Reintentar") { loadDataWorker() }.show()
                SharedUtils.dismissLoader(context)
            }
        })
    }

    private fun generateQR(source: String, height: Int = 512, width: Int = 512, color: Int): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) color else Color.WHITE)
        }
        return bitmap
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
            .into(binding.imageAvatar)
    }


    fun clickBtnBack() {
        findNavController().navigate(R.id.historialReservaBusFragment)
    }

    fun clickBtnCancel() {
        MaterialDialog.Builder(requireContext())
            .title("Alerta!")
            .content("Esta seguro de cancelar la Reserva?")
            .positiveText("Aceptar")
            .negativeText("Cancelar")
            .autoDismiss(true)
            .onPositive { dialog, which ->
                cancelReserve()
            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()


    }

    fun cancelReserve(){
        ShowLoader()
        viewModel.cancelReserveBus(
            RequestReservaBus(
                rut = reservaBus!!.workerId,
                usuario = reservaBus!!.workerId,
                idProg = reservaBus!!.codeProg
            )
        )
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

    private fun observeCancelReserveBusState() {
        viewModel.cancelReserveState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    ShowLoader()
                }
                is Resource.Success -> {
                    val message = it.data!![0].message ?: "Ocurrio un error cancelando la Reserva."
                    if(it.data!![0].status == "OK") {
                        binding.btnCancel.visibility = View.GONE
                        binding.imageQR.visibility=View.GONE
                        reservaBus!!.statusReserve = "NO"
                        DismissLoader()
                        showMessageSuccessCancelReserve(message)
                    }
                    else {
                        showMessageErrorCancelReserve(message)
                        DismissLoader()
                    }

                }
                is Resource.Error -> {
                    DismissLoader()
                    showMessageErrorCancelReserve("Ocurrio un error cancelando la Reserva.")
                }
            }
        }
    }





    fun showMessageErrorCancelReserve(message: String){
        MaterialDialog.Builder(requireContext())
            .title("Alerta!")
            .content(message)
            .positiveText("Aceptar")
            .autoDismiss(true)
            .onPositive { dialog, which -> dialog.dismiss()
            }
            .show()
    }

    fun showMessageSuccessCancelReserve(message: String){
        MaterialDialog.Builder(requireContext())
            .title("Alerta!")
            .content(message)
            .positiveText("Aceptar")
            .onPositive { dialog, which ->
                findNavController().navigate(R.id.historialReservaBusFragment)
            }
            .show()
    }





}