package com.webcontrol.android.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IAuthenticateListener
import com.webcontrol.android.data.db.entity.Usuarios
import com.webcontrol.android.data.model.Device
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.ActivityLoginBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.changepassword.VerifyChangePassActivity
import com.webcontrol.android.ui.login.dto.LoginDto
import com.webcontrol.android.ui.settings.EnableFingerPrintActivity
import com.webcontrol.android.ui.signup.SignupActivity
import com.webcontrol.android.util.FingerprintHandler
import com.webcontrol.android.util.FingerprintUtils.checkSensorState
import com.webcontrol.android.util.FingerprintUtils.cryptoObject
import com.webcontrol.android.util.PreferenceUtil
import com.webcontrol.android.util.PreferenceUtil.getBooleanPreference
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.getIMEI
import com.webcontrol.android.util.SharedUtils.getTermsAndCondition
import com.webcontrol.android.util.SharedUtils.getToken
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isFirtLogin
import com.webcontrol.android.util.SharedUtils.rememberUsernameChecked
import com.webcontrol.android.util.SharedUtils.setSession
import com.webcontrol.android.util.SharedUtils.setTermsandConditions
import com.webcontrol.android.util.SharedUtils.setUsuario
import com.webcontrol.android.util.SharedUtils.setUsuarioId
import com.webcontrol.android.util.SharedUtils.set_email
import com.webcontrol.android.util.SharedUtils.set_telefono
import com.webcontrol.android.util.SharedUtils.set_zip_code
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), IAuthenticateListener {

    private lateinit var binding: ActivityLoginBinding
    private var loader: MaterialDialog? = null
    private var terms: MaterialDialog? = null
    private var fingerprintManager: FingerprintManager? = null
    private var mPreferences: SharedPreferences? = null
    private var mFingerprintHandler: FingerprintHandler? = null
    private var dialog: MaterialDialog? = null
    private var lblTitle: TextView? = null
    private var lblSubtitle: TextView? = null
    private var isChangePassRequired = false
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // todo consider removing toolbar usage
        /*val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)*/
        MaterialAlertDialogBuilder(this)
            .setTitle("INFORMACION")
            .setMessage("Usuario de chile deben utilizar RUT")
            .setPositiveButton("ACEPTO") { dialog, which ->
                // Respond to positive button press
            }
            .show()
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //DrawOverLayPermissionGranted();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
        }
        val rememberUsernameChecked = rememberUsernameChecked(this)
        binding.switchRememberUsername!!.isChecked = rememberUsernameChecked
        if (rememberUsernameChecked) {
            val rememberedUsername = getUsuarioId(this)
            if (rememberedUsername != null) {
                binding.txtUsername!!.setText(rememberedUsername)
            }
        }
        binding.btnLogin!!.setOnClickListener {
            init()
        }
        binding.btnSignup!!.setOnClickListener {
            signUp()
        }
        binding.btnForgotMyPassword!!.setOnClickListener {
            change_my_password_verify()
        }
        binding.imageViewFinger!!.setOnClickListener{
            fingerBtn()
        }
        binding.txtUsername!!.filters = arrayOf<InputFilter>(AllCaps())
        binding.txtUsername!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().contains("-") ||
                    charSequence.toString().contains(".") ||
                    charSequence.toString().contains(" ")) {
                    binding.inputlayoutUsername!!.isErrorEnabled = true
                    binding.inputlayoutUsername!!.error = "No debe ingresar caracteres especiales ni espacios"
                    binding.inputlayoutUsername!!.errorIconDrawable = null
                } else {
                    binding.inputlayoutUsername!!.isErrorEnabled = false
                    binding.inputlayoutUsername!!.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onResume() {
        super.onResume()
        if (mPreferences!!.contains(KEY_PASSWORD) && getBooleanPreference(this, PreferenceUtil.PREF_FINGERPRINT, false)) initSensor()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager != null && fingerprintManager?.isHardwareDetected!!
                && getBooleanPreference(this, PreferenceUtil.PREF_FINGERPRINT, false)) {
            val finger = findViewById<View>(R.id.imageViewFinger) as ImageView
            finger.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        if (mFingerprintHandler != null) {
            mFingerprintHandler!!.cancel()
        }
    }

    private fun initSensor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager != null &&
                fingerprintManager!!.isHardwareDetected) {
            if (checkSensorState(this)) {
                val cryptoObject = cryptoObject
                if (cryptoObject != null) {
                    dialog = MaterialDialog.Builder(this@LoginActivity)
                            .customView(R.layout.dialog_fingerprint, false)
                            .positiveText("Cancelar")
                            .negativeText("Ingresar con clave")
                            .autoDismiss(false)
                            .cancelable(false)
                            .onPositive { materialDialog, dialogAction ->
                                materialDialog.cancel()
                                mFingerprintHandler!!.cancel()
                            }
                            .onNegative { materialDialog, dialogAction ->
                                materialDialog.dismiss()
                                mFingerprintHandler!!.cancel()
                            }.build()
                    dialog!!.getActionButton(DialogAction.NEGATIVE).visibility = View.INVISIBLE
                    val dv = dialog!!.getCustomView()
                    if (dv != null) {
                        lblTitle = dv.findViewById(R.id.lblTitle)
                        lblSubtitle = dv.findViewById(R.id.lblSubtitle)
                    }
                    dialog!!.show()
                    val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    mFingerprintHandler = FingerprintHandler(this, mPreferences!!, this)
                    mFingerprintHandler!!.startAuth(fingerprintManager, cryptoObject)
                }
            }
        }
    }

    override fun onAuthenticate(decryptPassword: String) {
        showLoader()
        dialog!!.dismiss()
        register()
    }

    override fun onAuthenticationFailed() {
        if (lblSubtitle != null) {
            lblSubtitle!!.setTextColor(Color.RED)
            lblSubtitle!!.text = getString(R.string.fingerprint_failed)
            dialog!!.getActionButton(DialogAction.NEGATIVE).visibility = View.VISIBLE
        }
    }

    override fun onAuthenticationError() {}

    fun init() {
        if (!areCredentialsValid()) return
        rememberUsernameChecked(this, binding.switchRememberUsername!!.isChecked)
        if (!getTermsAndCondition(this@LoginActivity)) {
            terms = MaterialDialog.Builder(this@LoginActivity)
                    .title("Términos y Condiciones")
                    .content(Html.fromHtml(getString(R.string.contentTerms)))
                    .checkBoxPromptRes(R.string.terms_agree, false, null)
                    .autoDismiss(false)
                    .cancelable(false)
                    .positiveText("Aceptar")
                    .negativeText("Cancelar")
                    .onNegative { dialog, which -> dialog.dismiss() }
                    .onPositive { dialog, which ->
                        if (dialog.isPromptCheckBoxChecked) {
                            setTermsandConditions(this@LoginActivity)
                            dialog.dismiss()
                            login()
                        } else {
                            showToast(this@LoginActivity, "No ha aceptado las condiciones")
                        }
                    }
                    .show()
        } else {
            login()
        }
    }

    private fun areCredentialsValid(): Boolean {
        var result = true
        val username = binding.txtUsername!!.text.toString()
        val password = binding.txtPassword!!.text.toString()
        val regexRut = getString(R.string.regex_rut)
        if (username.isEmpty()) {
            binding.inputlayoutUsername!!.error = "Ingrese ID"
            binding.inputlayoutUsername!!.isErrorEnabled = true
            result = false
        } else if (username.contains("-") || username.contains(".") || username.contains(" ")) {
            binding.inputlayoutUsername!!.error = "ID Incorrecto"
            binding.inputlayoutUsername!!.isErrorEnabled = true
            result = false
        } else binding.inputlayoutUsername!!.isErrorEnabled = false
        if (password.isEmpty()) {
            binding.inputlayoutPassword!!.error = "Ingrese su contraseña"
            binding.inputlayoutPassword!!.isErrorEnabled = true
            binding.inputlayoutPassword!!.errorIconDrawable = null
            result = false
        } else binding.inputlayoutPassword!!.isErrorEnabled = false
        return result
    }

    fun signUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    fun change_my_password_verify() {
        startActivity(Intent(this, VerifyChangePassActivity::class.java))
    }

    private fun showLoader() {
        loader = MaterialDialog.Builder(this)
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
    }

    fun login() {
        showLoader()
        val api = buildL()
        val username = binding.txtUsername!!.text.toString().replace(".", "").replace("-", "").replace(" ","")
        val password = binding.txtPassword!!.text.toString()
        val call = api.login(LoginDto(username, password))
        call.enqueue(object : Callback<ApiResponse<List<Usuarios>>?> {
            override fun onResponse(call: Call<ApiResponse<List<Usuarios>>?>, response: Response<ApiResponse<List<Usuarios>>?>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.data.isNotEmpty()) {
                        val usuario = result.data[0]
                        if (usuario.rut!= getUsuarioId(this@LoginActivity)) {
                            App.db.examenesDao().clean()
                            App.db.preguntasDao().clean()
                            App.db.respuestasDao().clean()
                            App.db.preaccesoDao().clean()
                            App.db.checkListDao().clean()
                            App.db.checklistGroupsDao().clean()
                            App.db.checkListDao().cleanChecklistDetalle()
                        }
                        App.db.usuariosDao().insertUser(usuario)
                        FirebaseCrashlytics.getInstance().setUserId(usuario.rut)
                        setUsuarioId(this@LoginActivity, usuario.rut)
                        set_email(this@LoginActivity, usuario.email)
                        set_zip_code(this@LoginActivity, usuario.zipCode)
                        set_telefono(this@LoginActivity, usuario.numCelular)
                        var user = "%s %s"
                        user = String.format(user, usuario.nombres, usuario.apellidos)
                        setUsuario(this@LoginActivity, user)
                        isChangePassRequired = usuario.isRequiereCambioPass
                        register()
                    } else {
                        loader!!.dismiss()
                        binding.inputlayoutPassword!!.error = "Acceso denegado"
                        binding.inputlayoutPassword!!.errorIconDrawable = null
                    }
                } else {
                    loader!!.dismiss()
                    showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Usuarios>>?>, t: Throwable) {
                loader!!.dismiss()
                showToast(applicationContext, TAG + " login() " + t.message)
            }
        })
    }

    fun register() {
        val api = buildL()
        val device = Device(
                getUsuarioId(this),
                getIMEI(this),
                getToken(this)
        )
        val call: Call<ApiResponse<Any>> = api.insertDevice(device)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                if (loader != null && loader!!.isShowing) {
                    loader!!.dismiss()
                }
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        subscribe()
                        setSession(this@LoginActivity, true)
                        if (isChangePassRequired) {
                            val intent = Intent(this@LoginActivity, ChangePasswordActivity::class.java)
                            startActivity(intent)
                        } else {
                            if (isFirtLogin(this@LoginActivity)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager != null && fingerprintManager!!.isHardwareDetected) {
                                    val intent = Intent(this@LoginActivity, EnableFingerPrintActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            } else {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        finish()
                    } else {
                        binding.inputlayoutPassword!!.error = "Acceso denegado"
                        binding.inputlayoutPassword!!.errorIconDrawable = null
                    }
                } else {
                    showToast(applicationContext, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                loader!!.dismiss()
                showToast(applicationContext, TAG + " register() " + t.message)
            }
        })
    }

    private fun subscribe() {
        if (!checkGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Instale google play services", Toast.LENGTH_LONG).show()
            return
        }
        val instance = FirebaseMessaging.getInstance()
        instance.subscribeToTopic(getUsuarioId(this))
                .addOnCompleteListener { task ->
                    var msg = "Dispositivo subscrito."
                    if (!task.isSuccessful) {
                        msg = "Error al subscribir dispositivo."
                    }
                    Log.d("Firebase", msg)
                    Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { Log.d(this@LoginActivity.javaClass.simpleName, "Firebase failure") }
    }

    private fun checkGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    ?.show()
            } else {
                Log.i(TAG, "This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }

    fun fingerBtn() {
        initSensor()
    }

    private fun DrawOverLayPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 0)
            }
        }
    }

    companion object {
        const val KEY_PASSWORD = "KEY_PASSWORD"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val TAG = "LoginActivity"
    }
}