package com.webcontrol.android.ui.changepassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.db.entity.WorkerUnregistered
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityChangePassVerifySaveBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ChangePassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePassVerifySaveBinding
    companion object {
        const val RUT = "RUT"
    }

    private var rut: String? = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePassVerifySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            rut = if (extras.getString(RUT) == null) extras.getString(RUT).toString() else extras.getString(RUT)
        }
        binding.rutCPSEditText.setText(rut)
        binding.changePassVerifySaveButton.setOnClickListener {
            changePassword()
        }
        binding.reenviarCodigoButton.setOnClickListener {
            resendCode()
        }
    }

    private fun changePassword() {
        if (!isValidInput()) {
            return
        }
        val loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
        val api = RestClient.buildL()
        val mWorker = Worker()
        mWorker.rut = binding.rutCPSEditText.text.toString()
        mWorker.codVerificacion=binding.codCPEditText.text.toString()
        mWorker.newPassword=binding.newPassEditText.text.toString()
        val call = api.changePasswordVerify(mWorker)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        SharedUtils.showToast(applicationContext, response.body()!!.message)
                        //startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                        finish()
                    } else SharedUtils.showToast(applicationContext, response.body()!!.message)
                } else {
                    SharedUtils.showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                loader.dismiss()
                SharedUtils.showToast(applicationContext, t.message)
            }
        })
    }

    private fun resendCode() {
        val loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
        val api = RestClient.buildL()
        val worker = WorkerUnregistered()
        worker.rut = binding.rutCPSEditText.text.toString()
        val call = api.sendSMS(worker)
        call.enqueue(object : Callback<ApiResponse<List<Worker>>> {
            override fun onResponse(call: Call<ApiResponse<List<Worker>>>, response: Response<ApiResponse<List<Worker>>>) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        SharedUtils.showToast(applicationContext, response.body()!!.message)
                    } else SharedUtils.showToast(applicationContext, response.body()!!.message)
                } else {
                    SharedUtils.showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Worker>>>, t: Throwable) {
                loader.dismiss()
                SharedUtils.showToast(applicationContext, t.message)
            }
        })
    }

    private fun isValidInput(): Boolean {
        var result = true
        val username = binding.rutCPSEditText.text.toString()
        val code= binding.codCPEditText.text.toString()
        val password = binding.newPassEditText.text.toString()
        val regexRut = getString(R.string.regex_rut)
        if (username.isEmpty()) {
            binding.rutTextCPSInputLayout.error = "Ingrese ID"
            binding.rutTextCPSInputLayout.isErrorEnabled = true
            result = false
        } else if (username.contains("-") || username.contains(".") || username.contains(" ")) {
            binding.rutTextCPSInputLayout.error = "ID Incorrecto"
            binding.rutTextCPSInputLayout.isErrorEnabled = true
            result = false
        } else binding.rutTextCPSInputLayout.isErrorEnabled = false
        if (code.isEmpty()) {
            binding.codTextCPInputLayout.error = "Ingrese un codigo valido"
            binding.codTextCPInputLayout.isErrorEnabled = true
            binding.codTextCPInputLayout.errorIconDrawable = null
            result = false
        } else binding.codTextCPInputLayout.isErrorEnabled = false

        if (password.isEmpty()) {
            binding.newPassInputLayout.error = "Ingrese su contraseña"
            binding.newPassInputLayout.isErrorEnabled = true
            binding.newPassInputLayout.errorIconDrawable = null
            result = false
        } else binding.newPassInputLayout.isErrorEnabled = false
        return result

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}