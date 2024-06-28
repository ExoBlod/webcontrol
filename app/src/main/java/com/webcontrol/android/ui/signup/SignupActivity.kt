package com.webcontrol.android.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.request.RequestOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Worker
import com.webcontrol.android.data.db.entity.WorkerUnregistered
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivitySignupBinding
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.PreferenceUtil
import com.webcontrol.android.util.PreferenceUtil.getBooleanPreference
import com.webcontrol.android.util.PreferenceUtil.setBooleanPreference
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.bitMapToByteArray
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    var mWorker: WorkerUnregistered? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result?.data != null) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap

                if (imageBitmap != null) {
                    loadImageToView(imageBitmap, binding.profilePictureImageview)
                    mWorker!!.foto = bitMapToByteArray(imageBitmap)
                } else {
                    showToast(this, getString(R.string.Photo_not_taken_try_again))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mWorker = WorkerUnregistered()
        binding.rutEditText.filters = arrayOf<InputFilter>(AllCaps())
        binding.zipCodePicker.setAutoDetectedCountry(true)
        if (!getBooleanPreference(this, PreferenceUtil.PREF_TUTORIAL_PHOTO, false)) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.take_picture_fab)
                .setPrimaryText("Toma una foto")
                .setSecondaryText("Debes tomarte una foto sosteniendo tu documento de identidad")
                .setBackgroundColour(ContextCompat.getColor(this, R.color.colorPrimary))
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        setBooleanPreference(
                            this@SignupActivity,
                            PreferenceUtil.PREF_TUTORIAL_PHOTO,
                            true
                        )
                        tutorialrut()
                    }
                }
                .show()
        }
        binding.takePictureFab.setOnClickListener {
            addPhotos()
        }
        binding.signUpButton.setOnClickListener {
            onSignUp()
        }
        binding.rutEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {Unit}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().contains("-") ||
                    charSequence.toString().contains(".") ||
                    charSequence.toString().contains(" ")
                ) {
                    binding.rutTextInputLayout.isErrorEnabled = true
                    binding.rutTextInputLayout.error =
                        "No debe ingresar caracteres especiales y/o espacios"
                    setBooleanPreference(
                        this@SignupActivity,
                        PreferenceUtil.PREF_TUTORIAL_PHOTO,
                        true
                    )
                    binding.rutTextInputLayout.errorIconDrawable = null
                } else {
                    binding.rutTextInputLayout.isErrorEnabled = false
                    binding.rutTextInputLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
                null
            }
        })
        mWorker?.let {
            it.foto?.let { data ->
                loadImageToView(
                    data,
                    binding.profilePictureImageview
                )
            }
        }
    }

    fun tutorialrut() {
        if (!getBooleanPreference(this, PreferenceUtil.PREF_RUT, false)) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.rutEditText)
                .setPrimaryText("ID")
                .setSecondaryText("El ID solo debe contener números y/o letras.")
                .setBackgroundColour(ContextCompat.getColor(this, R.color.colorPrimary))
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        setBooleanPreference(this@SignupActivity, PreferenceUtil.PREF_RUT, true)
                    }
                }
                .show()
        }
    }

    fun onSignUp() {
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
        val call = api.signUp(mWorker)
        call.enqueue(object : Callback<ApiResponse<List<Worker>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Worker>>>,
                response: Response<ApiResponse<List<Worker>>>
            ) {
                loader.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        showToast(applicationContext, response.body()!!.message)
                        if ((mWorker!!.emailAddress == null || mWorker!!.emailAddress!!.isEmpty()) && mWorker!!.celular != null && mWorker!!.celular!!.isEmpty()
                        ) {
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent =
                                Intent(this@SignupActivity, ValidarCodigoActivity::class.java)
                            intent.putExtra("RUT", binding.rutEditText.text.toString())
                            startActivity(intent)
                            finish()
                        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val isValidInput: Boolean
        private get() {
            var validRut = true
            var validLastName = true
            var validFirstName = true
            var validPhone = true
            var validEmailAddress = true
            var validPicture = true
            val rut = binding.rutEditText!!.text.toString()
            val firstName = binding.firstNameEditText!!.text.toString()
            val lastName = binding.lastNameEditText!!.text.toString()
            val emailAddress = binding.emailAddressEditText!!.text.toString()
            val cellPhone = binding.phoneNumberEditText!!.text.toString()
            if (rut.isEmpty() || rut.contains("-") || rut.contains(".") || rut.contains(" ")) {
                validRut = false
                binding.rutTextInputLayout.error = getString(R.string.enter_valid_id)
                binding.rutTextInputLayout.isErrorEnabled = true
            } else {
                binding.rutTextInputLayout.error = null
                binding.rutTextInputLayout.isErrorEnabled = false
            }
            if (lastName.isEmpty()) {
                validLastName = false
                binding.lastNameTextInputLayout.error = getString(R.string.enter_lastname)
                binding.lastNameTextInputLayout.isErrorEnabled = true
            } else binding.lastNameTextInputLayout.isErrorEnabled = false
            if (firstName.isEmpty()) {
                validFirstName = false
                binding.firstNameInputLayout.error = getString(R.string.enter_names)
                binding.firstNameInputLayout.isErrorEnabled = true
            } else binding.firstNameInputLayout.isErrorEnabled = false
            val regexEmail = getString(R.string.regex_email)
            if (emailAddress.isNotEmpty()) {
                if (!emailAddress.matches(regexEmail.toRegex())) {
                    validEmailAddress = false
                    binding.emailAddressInputLayout.error = getString(R.string.wrong_email)
                    binding.emailAddressInputLayout.isErrorEnabled = true
                } else binding.emailAddressInputLayout.isErrorEnabled = false
            }
            val regexPhone = getString(R.string.regex_cellphone)
            if (cellPhone.isNotEmpty()) {
                if (!cellPhone.matches(regexPhone.toRegex())) {
                    validPhone = false
                    binding.phoneNumberInputLayout.error = getString(R.string.enter_valid_number)
                    binding.phoneNumberInputLayout.isErrorEnabled = true
                } else binding.phoneNumberInputLayout.isErrorEnabled = false
            }
            if (mWorker!!.foto == null) {
                validPicture = false
                Toast.makeText(this, R.string.invalid_picture, Toast.LENGTH_LONG).show()
            }
            val isValidInput =
                validRut && validLastName && validFirstName && validEmailAddress && validPicture && validPhone
            if (!isValidInput) return isValidInput
            mWorker!!.apellidos = lastName
            mWorker!!.nombres = firstName
            mWorker!!.rut = rut.replace(".", "").replace("-", "").filterNot { it.isWhitespace() }
            mWorker!!.emailAddress = emailAddress
            mWorker!!.celular = cellPhone
            if (cellPhone.isNotEmpty()) {
                mWorker!!.zipcode = binding.zipCodePicker.selectedCountryCode
            } else {
                mWorker!!.zipcode = ""
            }
            return isValidInput
        }

    fun addPhotos() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    try {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        resultLauncher.launch(cameraIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response!!.isPermanentlyDenied) {
                        MaterialDialog.Builder(this@SignupActivity)
                            .title(R.string.app_name)
                            .content(getString(R.string.activate_camera_permission_take_photo))
                            .positiveText(android.R.string.ok)
                            .onPositive { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:$packageName")
                                )
                                startActivity(intent)
                                finish()
                            }
                            .negativeText(android.R.string.cancel)
                            .onNegative { dialog: MaterialDialog, _: DialogAction? ->
                                dialog.dismiss()
                            }
                            .autoDismiss(false)
                            .cancelable(false)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun loadImageToView(bitmap: Any, imageView: ImageView?) {
        try {
            var bm: Bitmap? = null
            var ba: ByteArray? = null
            if (bitmap is Bitmap) bm = bitmap else ba = bitmap as ByteArray
            GlideApp.with(this)
                .load(bm ?: ba)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .apply(RequestOptions.fitCenterTransform())
                .apply(RequestOptions.circleCropTransform())
                .into(imageView!!)
        } catch (ex: Exception) {
            showToast(applicationContext, ex.message)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}