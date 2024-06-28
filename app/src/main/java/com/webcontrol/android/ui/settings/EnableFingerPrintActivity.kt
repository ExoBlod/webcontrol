package com.webcontrol.android.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.IAuthenticateListener
import com.webcontrol.android.databinding.ActivityEnableFingerPrintBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.util.FingerprintHandler
import com.webcontrol.android.util.FingerprintUtils.checkSensorState
import com.webcontrol.android.util.FingerprintUtils.cryptoObject
import com.webcontrol.android.util.FingerprintUtils.encryptString
import com.webcontrol.android.util.PreferenceUtil
import com.webcontrol.android.util.PreferenceUtil.setBooleanPreference
import com.webcontrol.android.util.SharedUtils.setFirstLogin
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnableFingerPrintActivity : AppCompatActivity(), IAuthenticateListener {
    private lateinit var binding: ActivityEnableFingerPrintBinding
    private var mPreferences: SharedPreferences? = null
    private var fingerprintManager: FingerprintManager? = null
    private var mFingerprintHandler: FingerprintHandler? = null
    private var dialog: MaterialDialog? = null
    private var lblTitle: TextView? = null
    private var lblSubtitle: TextView? = null
    private var fromSettings = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_enable_finger_print)
        // todo consider to remove toolbar usage
        /*val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)*/
        binding.includefinger.btnAceptar.setOnClickListener {
            activar()
        }
        binding.includefinger.btnCancelar.setOnClickListener {
            cancelar()
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        }
        val extras = intent.extras
        if (extras != null) {
            fromSettings = extras.getBoolean("SETTINGS")
        }

    }

    override fun onStop() {
        super.onStop()
        if (mFingerprintHandler != null) {
            mFingerprintHandler!!.cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun activar() {
        if (checkSensorState(this)) {
            initSensor()
        } else {
            showToast(this, R.string.no_fingerprint_enrolled)
        }
    }

    fun cancelar() {
        if (!fromSettings) {
            setFirstLogin(this, false)
            val intent = Intent(this@EnableFingerPrintActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            cancelResult()
        }
    }

    private fun cancelResult() {
        val result = Intent()
        result.putExtra("RESULT", false)
        setResult(Activity.RESULT_CANCELED, result)
        finish()
    }

    private fun initSensor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager!!.isHardwareDetected) {
            if (checkSensorState(this)) {
                val cryptoObject = cryptoObject
                if (cryptoObject != null) {
                    dialog = MaterialDialog.Builder(this)
                            .customView(R.layout.dialog_fingerprint, false)
                            .positiveText("Cancelar")
                            .autoDismiss(false)
                            .cancelable(false)
                            .onPositive { materialDialog, dialogAction ->
                                materialDialog.cancel()
                                mFingerprintHandler!!.cancel()
                                if (!fromSettings) {
                                    setFirstLogin(this@EnableFingerPrintActivity, false)
                                    val intent = Intent(this@EnableFingerPrintActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    cancelResult()
                                }
                            }
                            .build()
                    val dv = dialog!!.customView
                    if (dv != null) {
                        lblTitle = dv.findViewById(R.id.lblTitle)
                        lblSubtitle = dv.findViewById(R.id.lblSubtitle)
                        lblSubtitle!!.text = getString(R.string.confirm_fingerprint)
                    }
                    dialog!!.show()
                    val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    mFingerprintHandler = FingerprintHandler(this, mPreferences!!, this)
                    mFingerprintHandler!!.startAuth(fingerprintManager, cryptoObject)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onAuthenticate(decryptPassword: String) {
        if (lblSubtitle != null) {
            lblSubtitle!!.setTextColor(Color.GREEN)
            lblSubtitle!!.text = getString(R.string.fingerprint_success)
        }
        val encoded = encryptString("webcontrol")
        mPreferences!!.edit().putString(LoginActivity.KEY_PASSWORD, encoded).apply()
        setBooleanPreference(this, PreferenceUtil.PREF_FINGERPRINT, true)
        setFirstLogin(this, false)
        val intent = Intent(this@EnableFingerPrintActivity, SuccessFingerprintActivity::class.java)
        if (fromSettings) {
            intent.putExtra("SETTINGS", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        }
        startActivity(intent)
        finish()
    }

    override fun onAuthenticationError() {}
    override fun onAuthenticationFailed() {
        if (lblSubtitle != null) {
            lblSubtitle!!.setTextColor(Color.RED)
            lblSubtitle!!.text = getString(R.string.fingerprint_failed)
        }
    }
}