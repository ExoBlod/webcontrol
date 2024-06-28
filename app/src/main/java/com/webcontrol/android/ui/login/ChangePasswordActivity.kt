package com.webcontrol.android.ui.login

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityChangePasswordBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.settings.EnableFingerPrintActivity
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.setFirstLogin
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    var nueva: String? = null
    var confirmar: String? = null
    private lateinit var fingerprintManager: FingerprintManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.includeid.btnCambiar.setOnClickListener {
            cambiar()
        }
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        }
        binding.includeid.txtNueva!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                nueva = charSequence.toString()
                confirmar = binding.includeid.txtConfirmar!!.text.toString()
                if (!TextUtils.isEmpty(confirmar)) {
                    if (nueva != confirmar) {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = true
                        binding.includeid.ilConfirmar!!.error = "Las contraseñas no coinciden"
                    } else {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = false
                        binding.includeid.ilConfirmar!!.error = null
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.includeid.txtConfirmar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                confirmar = charSequence.toString()
                nueva = binding.includeid.txtNueva.text.toString()
                if (!TextUtils.isEmpty(nueva)) {
                    if (nueva != confirmar) {
                        binding.includeid.ilConfirmar.isErrorEnabled = true
                        binding.includeid.ilConfirmar.error = "Las contraseñas no coinciden"
                    } else {
                        binding.includeid.ilConfirmar.isErrorEnabled = false
                        binding.includeid.ilConfirmar.error = null
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun cambiar() {
        if (!isValidInput) {
            return
        }
        val loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
        val api = buildL()
        val mWorker = Worker()
        mWorker.rut = getUsuarioId(this)
        mWorker.password =binding.includeid.txtActual.text.toString()
        mWorker.newPassword=binding.includeid.txtNueva.text.toString()
        val call: Call<ApiResponse<Any>> = api.changePassword(mWorker)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        showToast(applicationContext, "Contraseña actualizada")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this@ChangePasswordActivity::fingerprintManager.isInitialized && fingerprintManager!!.isHardwareDetected) {
                            val intent = Intent(this@ChangePasswordActivity, EnableFingerPrintActivity::class.java)
                            startActivity(intent)
                        } else {
                            setFirstLogin(this@ChangePasswordActivity, false)
                            val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    } else showToast(applicationContext, response.body()!!.message)
                } else {
                    showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                loader.dismiss()
                showToast(applicationContext, t.message)
            }
        })
    }

    val isValidInput: Boolean
        get() {
            var counter = 0
            if (TextUtils.isEmpty(binding.includeid.txtActual.text.toString())) {
                binding.includeid.ilActual.isErrorEnabled = true
                binding.includeid.ilActual.error = "Campo requerido"
                counter++
            } else {
                binding.includeid.ilActual.isErrorEnabled = false
                binding.includeid.ilActual.error = null
            }
            if (TextUtils.isEmpty(binding.includeid.txtNueva.text.toString())) {
                binding.includeid.ilNueva.isErrorEnabled = true
                binding.includeid.ilNueva.error = "Campo requerido"
                counter++
            } else {
                binding.includeid.ilNueva.isErrorEnabled = false
                binding.includeid.ilNueva.error = null
            }
            if (TextUtils.isEmpty(binding.includeid.txtConfirmar.text.toString())) {
                binding.includeid.ilConfirmar.isErrorEnabled = true
                binding.includeid.ilConfirmar.error = "Campo requerido"
                counter++
            } else {
                binding.includeid.ilConfirmar.isErrorEnabled = false
                binding.includeid.ilConfirmar.error = null
            }
            return counter == 0
        }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}