package com.webcontrol.android.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.data.network.dto.ChangeDataDto
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.get_telefono
import com.webcontrol.android.util.SharedUtils.get_zip_code
import com.webcontrol.android.util.SharedUtils.set_telefono
import com.webcontrol.android.util.SharedUtils.set_zip_code
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CambiarTelefonoViewModel(application: Application) : AndroidViewModel(application) {
    val CAMPO_OBLIGATORIO = "Campo obligatorio"

    @JvmField
    var extension = MutableLiveData<String?>()

    @JvmField
    var telefono = MutableLiveData<String?>()

    @JvmField
    var clave = MutableLiveData<String?>()

    @JvmField
    var codigo = MutableLiveData<String?>()
    var errorExtension = MutableLiveData<String?>()

    @JvmField
    var errorTelefono = MutableLiveData<String?>()

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
    private val TAG: String
    fun dataValida(): Boolean {
        var valido = true
        if (extension.value == null || extension.value!!.isEmpty()) {
            errorExtension.value = CAMPO_OBLIGATORIO
            valido = false
        } else errorExtension.setValue(null)
        if (telefono.value == null || telefono.value!!.isEmpty()) {
            errorTelefono.value = CAMPO_OBLIGATORIO
            valido = false
        } else errorTelefono.setValue(null)
        if (clave.value == null || clave.value!!.isEmpty()) {
            errorClave.value = CAMPO_OBLIGATORIO
            valido = false
        } else errorClave.setValue(null)
        if (telefono.value != null && extension.value == get_zip_code(getApplication()) && telefono.value == get_telefono(getApplication())) {
            errorTelefono.value = "Debe ingresar un teléfono distinto al actual"
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
        dto.zipCode = extension.value
        dto.numCelular = telefono.value
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
                step1.value = TAG + " enviarSolicitudCambio() " + t.message
            }
        })
    }

    fun reenviarSolicitudCambioTelefono() {
        step2.value = "pendiente"
        val api = buildL()
        val dto = ChangeDataDto()
        dto.rut = getUsuarioId(getApplication<Application>().applicationContext)
        dto.password = clave.value
        dto.zipCode = extension.value
        dto.numCelular = telefono.value
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
                step2.value = TAG + " reenviarSolicitudCambiotlefono() " + t.message
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
        dto.zipCode = extension.value
        dto.numCelular = telefono.value
        dto.codVerificacion = codigo.value
        val call: Call<ApiResponse<Any>> = api.changeData(dto)
        step2.value = "pendiente"
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isSuccess) {
                        set_zip_code(getApplication(), extension.value)
                        set_telefono(getApplication(), telefono.value)
                        step2.setValue("ok")
                    } else step2.setValue(response.body()!!.message)
                } else step2.setValue("Error, intente nuevamente")
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                step2.setValue(TAG + " enviarCodigoVerificacion() " + t.message)
            }
        })
    }

    init {
        verifyError.addSource(telefono) { o: Any? -> if (telefono.value == null || telefono.value!!.isEmpty()) errorTelefono.setValue(CAMPO_OBLIGATORIO) else errorTelefono.setValue(null) }
        verifyError.addSource(clave) { o: Any? -> if (clave.value == null || clave.value!!.isEmpty()) errorClave.setValue(CAMPO_OBLIGATORIO) else errorClave.setValue(null) }
        verifyError.addSource(codigo) { o: Any? -> if (codigo.value == null || codigo.value!!.isEmpty()) errorCodigo.setValue(CAMPO_OBLIGATORIO) else errorCodigo.setValue(null) }
        extension.value = get_zip_code(getApplication())
        telefono.value = get_telefono(getApplication())
        TAG = CambiarTelefonoViewModel::class.java.simpleName
    }
}