package com.webcontrol.android.ui.changepassword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityChangePassVerifyBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class VerifyChangePassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePassVerifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePassVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserFromSession()
        binding.changePassVerifyButton.setOnClickListener {
            changePasswordVerify()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadUserFromSession() {
        if (SharedUtils.getSession(this)) {
            val rut = SharedUtils.getUsuarioId(this)
            binding.rutCPEditText.setText(rut)
            binding.rutCPEditText.isEnabled = false
        }
    }

    private fun changePasswordVerify() {
        if (!isValidInput()) {
            return
        }
        val loader = MaterialDialog.Builder(this)
            .title(getString(R.string.processing))
            .content(getString(R.string.please_wait))
            .cancelable(false)
            .autoDismiss(false)
            .progress(true, 0)
            .show()
        val api = RestClient.buildL()
        val mWorker = Worker()
        mWorker.rut = binding.rutCPEditText!!.text.toString()
        val call = api.requestChangePasswordVerify(mWorker)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        SharedUtils.showToast(applicationContext, response.body()!!.message)
                        val intent =
                            Intent(this@VerifyChangePassActivity, ChangePassActivity::class.java)
                        intent.putExtra("RUT", binding.rutCPEditText.text.toString())
                        startActivity(intent)
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

    private fun isValidInput(): Boolean {
        var result = true
        val username = binding.rutCPEditText.text.toString()
        val regexRut = getString(R.string.regex_rut)
        if (username.isEmpty()) {
            binding.rutTextCPInputLayout.error = "Ingrese ID"
            binding.rutTextCPInputLayout.isErrorEnabled = true
            result = false
        } else if (username.contains("-") || username.contains(".") || username.contains(" ")) {
            binding.rutTextCPInputLayout.error = "ID Incorrecto"
            binding.rutTextCPInputLayout.isErrorEnabled = true
            result = false
        } else binding.rutTextCPInputLayout.isErrorEnabled = false

        return result
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}