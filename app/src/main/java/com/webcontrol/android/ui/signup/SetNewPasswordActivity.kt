package com.webcontrol.android.ui.signup

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputLayout
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityEnableFingerPrintBinding
import com.webcontrol.android.databinding.ActivityReservaBusDetalleBinding
import com.webcontrol.android.databinding.ActivitySetNewPasswordBinding
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.ui.signup.SetNewPasswordActivity
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class SetNewPasswordActivity : AppCompatActivity() {
    private var rut: String? = null
    private lateinit var binding: ActivitySetNewPasswordBinding
    private var fingerprintManager: FingerprintManager? = null
    var nueva: String? = null
    var confirmar: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            rut = if (extras.getString(RUT) == null) extras.getString(RUT)
                .toString() else extras.getString(RUT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager =
                getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
        }
        binding.includeid.btnCambiar.setOnClickListener {
            cambiar()
        }
        binding.includeid.txtNueva!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                nueva = charSequence.toString()
                confirmar = binding.includeid.txtConfirmar!!.text.toString()
                if (!TextUtils.isEmpty(confirmar)) {
                    if (nueva != confirmar) {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = true
                        binding.includeid.ilConfirmar!!.error = getString(R.string.passwords_do_not_match)
                    } else {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = false
                        binding.includeid.ilConfirmar!!.error = null
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.includeid.txtConfirmar!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                confirmar = charSequence.toString()
                nueva = binding.includeid.txtNueva!!.text.toString()
                if (!TextUtils.isEmpty(nueva)) {
                    if (nueva != confirmar) {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = true
                        binding.includeid.ilConfirmar!!.error = getString(R.string.passwords_do_not_match)
                    } else {
                        binding.includeid.ilConfirmar!!.isErrorEnabled = false
                        binding.includeid.ilConfirmar!!.error = null
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
        mWorker.rut = rut!!
        mWorker.password=""
        mWorker.newPassword=binding.includeid.txtNueva.text.toString()
        val call: Call<ApiResponse<Any>> = api.changePassword(mWorker)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        showToast(applicationContext, response.body()!!.message)
                        val intent = Intent(this@SetNewPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
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
            if (TextUtils.isEmpty(binding.includeid.txtNueva!!.text.toString())) {
                binding.includeid.ilNueva!!.isErrorEnabled = true
                binding.includeid.ilNueva!!.error = "Campo requerido"
                counter++
            } else {
                binding.includeid.ilNueva!!.isErrorEnabled = false
                binding.includeid.ilNueva!!.error = null
            }
            if (TextUtils.isEmpty(binding.includeid.txtConfirmar!!.text.toString())) {
                binding.includeid.ilConfirmar!!.isErrorEnabled = true
                binding.includeid.ilConfirmar!!.error = "Campo requerido"
                counter++
            } else {
                binding.includeid.ilConfirmar!!.isErrorEnabled = false
                binding.includeid.ilConfirmar!!.error = null
            }
            return counter == 0
        }

    override fun onBackPressed() {}

    companion object {
        const val RUT = "RUT"
    }
}