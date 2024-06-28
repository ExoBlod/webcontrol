package com.webcontrol.android.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.webcontrol.android.R
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.data.network.dto.ChangeDataDto
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.get_email
import com.webcontrol.android.util.SharedUtils.get_telefono
import com.webcontrol.android.util.SharedUtils.set_email
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CambiarEmailViewModel(application: Application) : AndroidViewModel(application) {
    val CAMPO_OBLIGATORIO = "Campo obligatorio"

    @JvmField
    var email = MutableLiveData<String?>()

    @JvmField
    var clave = MutableLiveData<String?>()

    @JvmField
    var codigo = MutableLiveData<String?>()

    @JvmField
    var errorEmail = MutableLiveData<String?>()

    @JvmField
    var errorClave = MutableLiveData<String?>()

    @JvmField
    var errorCodigo = MutableLiveData<String?>()

    @JvmField
    var verifyError: MediatorLiveData<Any> = MediatorLiveData<Any>()

    @JvmField
    var step1 = MutableLiveData<String?>()

    @JvmField
    var step2 = MutableLiveData<String?>()
    fun dataValida(): Boolean {
        val regexEmail = getApplication<Application>().getString(R.string.regex_email)
        var valido = true
        if (email.value == null || email.value!!.isEmpty() || !email.value!!.matches(regexEmail.toRegex())) {
            errorEmail.value = CAMPO_OBLIGATORIO
            if (!email.value!!.matches(regexEmail.toRegex())) errorEmail.value = "Correo electrónico inválido"
            valido = false
        } else errorEmail.setValue(null)
        if (clave.value == null || clave.value!!.isEmpty()) {
            errorClave.value = CAMPO_OBLIGATORIO
            valido = false
        } else errorClave.setValue(null)
        if (email.value != null && email.value == get_telefono(getApplication())) {
            errorEmail.value = "Debe ingresar un correo electrónico distinto al actual"
            valido = false
        }
        return valido
    }

    fun dataConfirmValida(): Boolean {
        var valido = true
        if (codigo.value == null || codigo.value!!.isEmpty()) {
            errorCodigo.value = CAMPO_OBLIGATORIO
            valido = false
        } else errorCodigo.setValue(null)
        return valido
    }

    fun limpiarSteps() {
        if (step1.value != null) {
            if (step1.value != "pendiente") {
                step1.value = null
            }
        }
        if (step2.value != null) {
            if (step2.value != "pendiente") {
                step2.value = null
            }
        }
    }

    fun enviarSolicitudCambioTelefono() {
        val api = buildL()
        val dto = ChangeDataDto()
        dto.rut = getUsuarioId(getApplication<Application>().applicationContext)
        dto.password = clave.value
        dto.email = email.value
        val call: Call<ApiResponse<Any>> = api.verifyChangeData(dto)
        step1.value = "pendiente"
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isSuccess) {
                        step1.setValue("ok")
                    } else step1.setValue(response.body()!!.message)
                } else step1.setValue("Error, intente nuevamente")
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                step1.value = t.message
            }
        })
    }

    fun reenviarSolicitudCambioEmail() {
        step2.value = "pendiente"
        val api = buildL()
        val dto = ChangeDataDto()
        dto.rut = getUsuarioId(getApplication<Application>().applicationContext)
        dto.password = clave.value
        dto.email = email.value
        val call: Call<ApiResponse<Any>> = api.verifyChangeData(dto)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isSuccess) {
                        step2.setValue("ok-reenvio")
                    } else step2.setValue(response.body()!!.message)
                } else step2.setValue("Error, intente nuevamente")
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                step2.setValue(t.message)
            }
        })
    }

    fun limpiarCod() {
        codigo.value = null
        errorCodigo.value = null
    }

    fun enviarCodigoVerificacion() {
        val api = buildL()
        val dto = ChangeDataDto()
        dto.rut = getUsuarioId(getApplication<Application>().applicationContext)
        dto.email = email.value
        dto.codVerificacion = codigo.value
        val call: Call<ApiResponse<Any>> = api.changeData(dto)
        step2.value = "pendiente"
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isSuccess) {
                        set_email(getApplication(), email.value)
                        step2.setValue("ok")
                    } else step2.setValue(response.body()!!.message)
                } else step2.setValue("Error, intente nuevamente")
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                step2.value = t.message
            }
        })
    }

    init {
        verifyError.addSource(email) { o: Any? -> if (email.value == null || email.value!!.isEmpty()) errorEmail.setValue(CAMPO_OBLIGATORIO) else errorEmail.setValue(null) }
        verifyError.addSource(clave) { o: Any? -> if (clave.value == null || clave.value!!.isEmpty()) errorClave.setValue(CAMPO_OBLIGATORIO) else errorClave.setValue(null) }
        verifyError.addSource(codigo) { o: Any? -> if (codigo.value == null || codigo.value!!.isEmpty()) errorCodigo.setValue(CAMPO_OBLIGATORIO) else errorCodigo.setValue(null) }
        email.value = get_email(getApplication())
    }
}