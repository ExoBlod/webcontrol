package com.webcontrol.android.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.fingerprint.FingerprintManager
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.changepassword.VerifyChangePassActivity
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.ui.onboarding.OnBoardingPermission
import com.webcontrol.android.util.PreferenceUtil
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.hasPermission
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), IOnBackPressed {
    private val REQUEST_CODE = 100
    private var prefFingerprint: Preference? = null
    private var prefCambiarClave: Preference? = null
    private var prefTelefono: Preference? = null
    private var prefEmail: Preference? = null
    private var prefPermission: Preference? = null
    private var prefGrantLocation: Preference? = null
    private var fingerprintManager: FingerprintManager? = null
    private var prefPrivacyPolicy: Preference? = null
    private var prefDeleteAccount: Preference? = null
    private var _url = "https://webcontrol.webcontrol.cl/manuales/WebControl_Acuerdo_Uso.asp"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager =
                requireActivity().getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager?
        }
        setupActions()
    }

    private fun hasFingerprintSupport(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (fingerprintManager != null) {
                fingerprintManager?.isHardwareDetected!!
            } else {
                false
            }
        }
        return false
    }

    private fun setupActions() {
        prefFingerprint = preferenceManager.findPreference(PreferenceUtil.PREF_FINGERPRINT)
        if (prefFingerprint != null) {
            prefFingerprint!!.isVisible = hasFingerprintSupport()
            prefFingerprint!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any ->
                    try {
                        val value = java.lang.Boolean.parseBoolean(newValue.toString())
                        if (value) {
                            val intent = Intent(context, EnableFingerPrintActivity::class.java)
                            intent.putExtra("SETTINGS", true)
                            startActivityForResult(intent, REQUEST_CODE)
                        } else {
                            return@OnPreferenceChangeListener true
                        }
                    } catch (e: Exception) {
                        return@OnPreferenceChangeListener false
                    }
                    false
                }
        }
        prefCambiarClave = preferenceManager.findPreference(PreferenceUtil.PREF_CAMBIAR_CLAVE)
        if (prefCambiarClave != null) {
            prefCambiarClave!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    startActivity(Intent(activity, VerifyChangePassActivity::class.java))
                    true
                }
        }
        prefTelefono = preferenceManager.findPreference(PreferenceUtil.PREF_TELEFONO)
        if (prefTelefono != null) {
            prefTelefono!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    startActivity(Intent(context, CambiarTelefonoActivity::class.java))
                    true
                }
        }
        prefEmail = preferenceManager.findPreference(PreferenceUtil.PREF_EMAIL)
        if (prefEmail != null) {
            prefEmail!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    startActivity(Intent(context, CambiarEmailActivity::class.java))
                    true
                }
        }
        prefPermission = preferenceManager.findPreference(PreferenceUtil.PREF_UBICACION)
        if (prefPermission != null) {
            prefPermission!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(context, CambiarPermisosActivity::class.java))
                true
            }
        }
        prefGrantLocation = preferenceManager.findPreference(PreferenceUtil.PREF_GRANT_LOCATION)
        if (prefGrantLocation != null) {
            prefGrantLocation!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    try {
                        val value = java.lang.Boolean.parseBoolean(newValue.toString())
                        if (value) {
                            (activity as MainActivity).locationService!!.requestLocationUpdates()
                            askGpsProvider()
                        } else {
                            (activity as MainActivity).locationService!!.removeLocationUpdates()
                        }
                        return@OnPreferenceChangeListener true
                    } catch (e: Exception) {
                        return@OnPreferenceChangeListener false
                    }
                }
        }
        prefPrivacyPolicy = preferenceManager.findPreference(PreferenceUtil.PREF_PRIVACY_POLICY)
        if (prefPrivacyPolicy != null) {
            prefPrivacyPolicy!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val _link = Uri.parse(_url)
                val i = Intent(Intent.ACTION_VIEW, _link)
                startActivity(i)
                true
            }
        }
        prefDeleteAccount = preferenceManager.findPreference(PreferenceUtil.PREF_DELETE_ACCOUNT)
        if (prefDeleteAccount != null) {
            prefDeleteAccount!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                dialogDeleteAccount(SharedUtils.getUsuarioId(requireContext()))
                true
            }
        }
    }

    private fun dialogDeleteAccount(rut: String) {
        MaterialDialog.Builder(requireContext())
            .title("¿Estás seguro de esta acción?")
            .content("Si eliminas tu cuenta, perderás toda la información almacenada.")
            .positiveText("ACEPTAR")
            .positiveColor(Color.GRAY)
            .negativeText("CANCELAR")
            .cancelable(false)
            .onPositive { dialog, which ->
                closedSession(dialog, rut)
            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun closedSession(dialog: MaterialDialog, rut: String) {
        val api = RestClient.buildL()
        val call = api.accountDeleted(rut)
        call.enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                dialog.dismiss()
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        FirebaseCrashlytics.getInstance().setUserId("")
                        DetachFirebaseTask().execute()
                        SharedUtils.setSession(requireContext(), false)
                        SharedUtils.showToast(requireContext(), response.body()!!.message)
                        activity!!.finish()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                    } else SharedUtils.showToast(requireContext(), response.body()!!.message)
                } else {
                    SharedUtils.showToast(requireContext(), response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                dialog.dismiss()
                SharedUtils.showToast(requireContext(), t.message)
            }
        })
    }

    private class DetachFirebaseTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String? {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun askGpsProvider() {
        if (!(activity as MainActivity).locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            SharedUtils.showDialog(
                requireContext(),
                getString(R.string.title_dialog_alert),
                getString(R.string.text_dialog_ask_enable_gps),
                getString(R.string.buttonActivate),
                { dialog, _ ->
                    dialog.dismiss()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                },
                getString(R.string.buttonNegative),
                { dialog, _ -> dialog.dismiss() }
            )
        } else {
            if (SharedUtils.getRequestLocationStatus(requireContext())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val permissionApproved =
                        (activity as MainActivity).hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    if (!permissionApproved) {
                        askBackgroundLocation()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askBackgroundLocation() {
        SharedUtils.showDialog(
            requireContext(),
            getString(R.string.title_dialog_alert),
            getString(R.string.background_location_permission_rationale),
            getString(R.string.buttonAllow),
            { dialog, _ ->
                dialog.dismiss()
                checkBackgroundLocationPermission()
            },
            getString(R.string.buttonNegative),
            { dialog, _ -> dialog.dismiss() }
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkBackgroundLocationPermission() {
        val permissionApproved =
            (activity as MainActivity).hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (!permissionApproved) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                startActivity(Intent(requireContext(), OnBoardingPermission::class.java))
            } else {
                (activity as MainActivity).requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (prefGrantLocation as SwitchPreference).isChecked =
            SharedUtils.getRequestLocationStatus(requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (activity != null) {
                    requireActivity().recreate()
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }

        fun newInstance(param: String?): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}