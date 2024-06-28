package com.webcontrol.android.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.db.entity.WorkerUnregistered
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityValidarCodigoBinding
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ValidarCodigoActivity : AppCompatActivity() {
    private var rut: String? = null
    private lateinit var binding: ActivityValidarCodigoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidarCodigoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_validar_codigo)
        val extras = intent.extras
        if (extras != null) {
            rut = if (extras.getString(RUT) == null) extras.getString(RUT)
                .toString() else extras.getString(RUT)
        }
        binding.btnValidarCodigo.setOnClickListener {
            btnValidarCodigo()
        }
        binding.btnReenviarCodigo.setOnClickListener {
            btnReenviarCodigo()
        }
    }

    fun btnValidarCodigo() {
        val codigo = binding.codigoEditText.text.toString()
        if (codigo.isEmpty()) {
            binding.codigoTextInputLayout.error = getString(R.string.enter_validation_code)
            binding.codigoTextInputLayout.isErrorEnabled = true
            return
        } else binding.codigoTextInputLayout.isErrorEnabled = false
        val loader = MaterialDialog.Builder(this)
            .title(getString(R.string.loading))
            .content(getString(R.string.please_wait))
            .cancelable(false)
            .autoDismiss(false)
            .progress(true, 0)
            .show()
        val api = buildL()
        val workerUnregistered = WorkerUnregistered()
        workerUnregistered.rut = rut
        workerUnregistered.codVerificacion=codigo
        val call = api.validarSMS(workerUnregistered)
        call.enqueue(object : Callback<ApiResponse<List<Worker>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Worker>>>,
                response: Response<ApiResponse<List<Worker>>>
            ) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        binding.codigoTextInputLayout.isErrorEnabled = false
                        val intent =
                            Intent(this@ValidarCodigoActivity, SetNewPasswordActivity::class.java)
                        intent.putExtra("RUT", rut)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.codigoTextInputLayout.error = response.body()!!.message
                        binding.codigoTextInputLayout.isErrorEnabled = true
                    }
                } else {
                    showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Worker>>>, t: Throwable) {
                loader.dismiss()
                showToast(applicationContext, t.message)
            }
        })
    }

    fun btnReenviarCodigo() {
        val loader = MaterialDialog.Builder(this)
            .title(getString(R.string.loading))
            .content(getString(R.string.please_wait))
            .cancelable(false)
            .autoDismiss(false)
            .progress(true, 0)
            .show()
        val api = buildL()
        val workerUnregistered = WorkerUnregistered()
        workerUnregistered.rut = rut
        val call = api.sendSMS(workerUnregistered)
        call.enqueue(object : Callback<ApiResponse<List<Worker>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Worker>>>,
                response: Response<ApiResponse<List<Worker>>>
            ) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        showToast(applicationContext, response.body()!!.message)
                    } else showToast(applicationContext, response.body()!!.message)
                } else {
                    showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Worker>>>, t: Throwable) {
                loader.dismiss()
                showToast(applicationContext, t.message)
            }
        })
    }

    override fun onBackPressed() {}

    companion object {
        const val RUT = "RUT"
    }
}