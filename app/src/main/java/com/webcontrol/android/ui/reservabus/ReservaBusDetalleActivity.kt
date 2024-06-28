package com.webcontrol.android.ui.reservabus

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.ActivityReservaBusDetalleBinding
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.LocalStorage
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class ReservaBusDetalleActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReservaBusDetalleBinding
    private var ProgId = 0
    private var WorkerId: String? = null
    private var TransactionId: String? = null
    private val localStorage by inject<LocalStorage>()
    private var colorHandler: Handler? = null
    private var clockHandler: Handler? = null
    lateinit var localClock: Clock

    private var changeColor = object : Runnable {
        override fun run() {
            val colorQr = selectColor()
            binding.includereserva.imageQr?.setImageBitmap(generateQR(TransactionId!!, color = colorQr))
            colorHandler!!.postDelayed(this, 3000)
        }
    }

    private var updateLocalClock = object : Runnable {
        override fun run() {
            localClock.tick()
            binding.includereserva.lblHora.text = localClock.toString()
            clockHandler!!.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservaBusDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_reserva_bus_detalle)
        setSupportActionBar(binding.toolbar)

        val extras = intent.extras
        if (extras != null) {
            ProgId = extras.getInt("PROG_ID")
            WorkerId = extras.getString("WORKER_ID")
        }

        title = getString(R.string.reserve_bus)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        colorHandler = Handler()
        clockHandler = Handler()

        binding.includereserva.lblFecha.text = SharedUtils.getNiceDate(SharedUtils.wCDate)
        binding.includereserva.lblHora.text = SharedUtils.time
        localClock = Clock(SharedUtils.time)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        colorHandler!!.post(changeColor)
        clockHandler!!.post(updateLocalClock)
    }

    override fun onPause() {
        super.onPause()
        colorHandler!!.removeCallbacks(changeColor)
        clockHandler!!.removeCallbacks(updateLocalClock)
    }

    private fun loadData() {
        val reservaBus = App.db.reservaBusDao().selectReservaByProgIdAndWorkerId(ProgId, WorkerId!!)

        if (reservaBus != null) {
            val colorQr = selectColor()
            TransactionId = reservaBus.transactionId!!
            binding.includereserva.imageQr.setImageBitmap(generateQR(TransactionId!!, color = colorQr))
            binding.includereserva.textNroAsiento.text = reservaBus.asiento.toString()
            binding.includereserva.textOrigen.text = reservaBus.origenName
            binding.includereserva.textDestino.text = reservaBus.destinoName
            binding.includereserva.textFecha.text = SharedUtils.getNiceDate(reservaBus.fecha)
            binding.includereserva.textHora.text = reservaBus.hora
        }
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

    private fun selectColor(): Int {
        var colorRandom = (1..10).random()
        val colorSafe: Int =  localStorage[Constants.VAL_COLOR]!!
        while (colorRandom == colorSafe) {
            colorRandom = (1..10).random()
        }
        localStorage[Constants.VAL_COLOR] = colorRandom
        var colorInt = 0

        when (colorRandom) {
            1 -> colorInt = Color.BLACK
            2 -> colorInt = Color.BLUE
            3 -> colorInt = Color.CYAN
            4 -> colorInt = Color.parseColor("#D4AC0D") //Dark Yellow
            5 -> colorInt = Color.MAGENTA
            6 -> colorInt = Color.RED
            7 -> colorInt = Color.parseColor("#17A589") //Dark Green
            8 -> colorInt = Color.parseColor("#800000") //Maroon
            9 -> colorInt = Color.parseColor("#800080") //Purple
            10 -> colorInt = Color.parseColor("#E67E22") //Orange
        }

        return colorInt
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}