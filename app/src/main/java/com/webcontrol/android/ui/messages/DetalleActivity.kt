package com.webcontrol.android.ui.messages

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.amulyakhare.textdrawable.TextDrawable
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.AppDataBase
import com.webcontrol.android.data.db.entity.Message
import com.webcontrol.android.data.db.entity.MessageHistory
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityDetalleBinding
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.date
import com.webcontrol.android.util.SharedUtils.getNiceDate
import com.webcontrol.android.util.SharedUtils.getRandomMaterialColor
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.workers.SyncMessageState
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class DetalleActivity : AppCompatActivity() {
    private var mensajeId: String? = null
    private lateinit var binding: ActivityDetalleBinding
    private var TAG: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = "Mensaje"
        val extras = intent.extras
        if (extras != null) {
            mensajeId = extras.getString(MESSAGE_ID)
            if (mensajeIdExists()) loadMensaje(mensajeId) else sync()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        TAG = DetalleActivity::class.java.simpleName

    }

    fun setImportante(isImportant: Boolean) {
        if (isImportant) {
            binding.includeid.iconStar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_star_black_24dp))
            binding.includeid.iconStar.setColorFilter(ContextCompat.getColor(applicationContext, R.color.icon_tint_selected))
        } else {
            binding.includeid.iconStar.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_star_border_black_24dp))
            binding.includeid.iconStar.setColorFilter(ContextCompat.getColor(applicationContext, R.color.icon_tint_normal))
        }
    }

    private fun mensajeIdExists(): Boolean {
        return mensajeId != null && App.db.messageDao().getOne(mensajeId) != null
    }

    fun loadMensaje(mensajeId: String?) {
        val message = App.db.messageDao().getOne(mensajeId.toString()) ?: return
        setImportante(message.isImportant)
        val color = message.color
        val drawable = TextDrawable.builder().buildRound(if (message.asunto.isNullOrEmpty()) "S" else message.asunto!!.substring(0, 1).toUpperCase(), color)
        binding.includeid.img.setImageDrawable(drawable)
        binding.includeid.lblAsunto.text = if (message.asunto.isNullOrEmpty()) "Sin Asunto" else message.asunto!!.replace("<[^>]*>".toRegex(), "")
        var fecha = "Fecha: %s"
        fecha = String.format(fecha, getNiceDate(message.fecha))
        binding.includeid.lblFecha.text = fecha
        var hora = "Hora: %s"
        hora = String.format(hora, message.hora)
        binding.includeid.lblHora.text = hora
        binding.includeid.lblRemitente.text = message.remitente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) binding.includeid.lblBody.text = Html.fromHtml(message.mensaje, Html.FROM_HTML_MODE_COMPACT) else binding.includeid.lblBody.text = Html.fromHtml(message.mensaje)
        if (message.estado != 2) {
            message.estado = 2
            message.fechaModificacion = "$date $time"
            App.db.messageDao().update(message)
            App.db.messageDao().updateEstadoSync(message.id, false)
            val messageHistory = MessageHistory()
            messageHistory.actionId = 2
            messageHistory.messageId = message.id
            messageHistory.date = date
            val messageHistoryDao = App.db.messageHistoryDao()
            messageHistoryDao.insert(messageHistory)
        }
        if (!message.estadoSincronizado) updateMessageStatusOnServer(message)
        if (!message.importanteSincronizado) updateMessageImportantOnServer(message)

        binding.includeid.iconStar.setOnClickListener {
            iconStarClick()
        }
    }
    fun iconStarClick() {
        val message = App.db.messageDao().getOne(mensajeId.toString())
        if (message != null) {
            setImportante(!message.isImportant)
            message.isImportant = !message.isImportant
            AppDataBase.getInstance(applicationContext).messageDao().update(message)
            AppDataBase.getInstance(applicationContext).messageDao().updateImportanteSync(message.id, false)
            updateMessageImportantOnServer(message)
        }
    }

    private fun updateMessageStatusOnServer(message: Message) {
        val api = buildL()
        val call: Call<ApiResponse<Any>> = api.messageRead(message)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                App.db.messageDao().updateEstadoSync(message.id, true)
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                showToast(this@DetalleActivity, TAG + " updateMessageStatusOnServer() " + t.message)
            }
        })
    }

    private fun updateMessageImportantOnServer(message: Message) {
        val api = buildL()
        val call: Call<ApiResponse<Any>> = api.messageImportant(message)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                App.db.messageDao().updateImportanteSync(message.id, true)
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                showToast(this@DetalleActivity, TAG + " updateMessageImportantOnServer() " + t.message)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    var dialog: MaterialDialog? = null
    fun sync() {
        dialog = MaterialDialog.Builder(this).title("Cargando...").autoDismiss(false).progress(true, 0).cancelable(false).content("Espere por favor").build()
        dialog!!.show()
        val syncEstados = OneTimeWorkRequest.Builder(SyncMessageState::class.java).build()
        WorkManager.getInstance().enqueue(syncEstados)
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncEstados.id).observe(this, Observer { workInfo: WorkInfo? ->
            if (workInfo != null && workInfo.state.isFinished) {
                val resultado = workInfo.outputData.getString("resultado")
                if (resultado != null && resultado == "ok") {
                    val idSync = App.db.messageDao().getMaxIdSync(getUsuarioId(applicationContext))
                    val api = buildL()
                    val call = api.getMessages(getUsuarioId(applicationContext), idSync)
                    call.enqueue(object : Callback<ApiResponse<List<Message>>?> {
                        override fun onResponse(call: Call<ApiResponse<List<Message>>?>, response: Response<ApiResponse<List<Message>>?>) {
                            dialog!!.hide()
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result != null && result.isSuccess) {
                                    for (i in result.data.indices) {
                                        val msg = result.data[i]
                                        val exist = App.db.messageDao().getOne(msg.id.toString())
                                        if (exist != null) App.db.messageDao().updateMessage(msg.id, msg.estado, msg.isImportant, msg.idSync) else {
                                            msg.color = getRandomMaterialColor(applicationContext, "400")
                                            App.db.messageDao().insert(msg)
                                        }
                                    }
                                    loadMensaje(mensajeId)
                                }
                            } else {
                                showToast(applicationContext, response.message())
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<List<Message>>?>, t: Throwable) {
                            showToast(applicationContext, TAG + " sync() " + t.message)
                            dialog!!.hide()
                            reintentar()
                        }
                    })
                } else {
                    dialog!!.hide()
                    reintentar()
                    showToast(applicationContext, "Error al obtener el mensaje, intente nuevamente")
                }
            }
        })
    }

    private fun reintentar() {
        MaterialDialog.Builder(this)
                .title("Error al cargar")
                .content("¿Desea intentar nuevamente?")
                .positiveText("Reintentar")
                .negativeText("Cancelar")
                .onPositive { dialog1: MaterialDialog?, which: DialogAction? -> sync() }
                .onNegative { dialog1: MaterialDialog?, which: DialogAction? -> finish() }
                .autoDismiss(false).show()
    }

    companion object {
        const val MESSAGE_ID = "MESSAGE_ID"
    }
}