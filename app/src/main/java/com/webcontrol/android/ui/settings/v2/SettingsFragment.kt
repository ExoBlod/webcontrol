package com.webcontrol.android.ui.settings.v2

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.webcontrol.android.R
import com.webcontrol.android.common.GenericAdapter
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.ui.changepassword.VerifyChangePassActivity
import com.webcontrol.android.ui.common.SettingItemTypeFactory
import com.webcontrol.android.ui.common.enums.SettingOption
import com.webcontrol.android.ui.common.widgets.SettingItem
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.ui.settings.CambiarEmailActivity
import com.webcontrol.android.ui.settings.CambiarPermisosActivity
import com.webcontrol.android.ui.settings.CambiarTelefonoActivity
import com.webcontrol.android.ui.settings.EnableFingerPrintActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.rvSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Ajustes"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()

        val settings = mutableListOf<SettingItem>()

        val hasFingerPrintSupport = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SharedUtils.hasFingerPrintSupport(requireContext())
        } else {
            false
        }

        if (hasFingerPrintSupport) {
            settings.add(
                SettingItem(
                    SettingOption.ENABLE_FINGERPRINT,
                    "Huella digital",
                    "Usar la huella digital para ingresar a su cuenta WebControl",
                    R.drawable.ic_fingerprint_black
                )
            )
        }

        /*settings.add(
            SettingItem(
                SettingOption.ENABLE_TRACKING,
                "Ubicación",
                "Habilitar el seguimiento de su ubicación mediante WebControl",
                R.drawable.ic_outline_gps_fixed_72
            )
        )*/

        settings.add(
            SettingItem(
                SettingOption.CHANGE_PASSWORD,
                "Actualizar contraseña",
                "Cambie la contraseña de acceso a su cuenta de WebControl",
                R.drawable.ic_lock_outline_black_24dp
            )
        )

        settings.add(
            SettingItem(
                SettingOption.CHANGE_PHONE,
                "Actualizar teléfono",
                "Cambie el nro de teléfono asociado a su cuenta de WebControl",
                R.drawable.ic_phone_android_black_24dp
            )
        )

        settings.add(
            SettingItem(
                SettingOption.CHANGE_EMAIL,
                "Actualizar correo electrónico",
                "Cambie la dirección de correo electrónico asociada a su cuenta de WebControl",
                R.drawable.ic_email_black_24dp
            )
        )

        settings.add(
            SettingItem(
                SettingOption.PRIVACY_POLICY,
                "Política de Privacidad",
                "Consulte las politicas de Privacidad de la aplicación",
                R.drawable.ic_info
            )
        )

        settings.add(
            SettingItem(
                SettingOption.DELETE_ACCOUNT,
                "Eliminar cuenta",
                "Elimina tu cuenta definitavamente",
                R.drawable.ic_borrar_usuario
            )
        )

        showList(settings)
    }

    private fun setupList() {
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val layoutManager = LinearLayoutManager(context)
        rvSettings.layoutManager = layoutManager
        rvSettings.addItemDecoration(divider)
        rvSettings.adapter = null
    }

    private fun showList(data: List<SettingItem>) {
        val adapter = GenericAdapter(SettingItemTypeFactory())
        rvSettings.adapter = adapter
        adapter.setItems(data)
        adapter.setOnClickListener { item ->
            val settingItem = item as SettingItem
            when (settingItem.option) {
                SettingOption.ENABLE_FINGERPRINT -> {
                    val intent = Intent(context, EnableFingerPrintActivity::class.java)
                    intent.putExtra("SETTINGS", true)
                    startActivityForResult(intent, REQUEST_CODE)
                }

                SettingOption.ENABLE_TRACKING -> {
                    startActivity(Intent(context, CambiarPermisosActivity::class.java))
                }

                SettingOption.CHANGE_PASSWORD -> {
                    startActivity(Intent(activity, VerifyChangePassActivity::class.java))
                }

                SettingOption.CHANGE_PHONE -> {
                    startActivity(Intent(context, CambiarTelefonoActivity::class.java))
                }

                SettingOption.CHANGE_EMAIL -> {
                    startActivity(Intent(context, CambiarEmailActivity::class.java))
                }

                SettingOption.PRIVACY_POLICY -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(POLICY_URL)))
                }

                SettingOption.DELETE_ACCOUNT -> {
                    dialogDeleteAccount(SharedUtils.getUsuarioId(requireContext()))
                }
            }
        }
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
                        FirebaseInstallations.getInstance().delete().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Firebase", "instance: deleted")
                            }
                        }
                        SharedUtils.setSession(requireContext(), false)
                        SharedUtils.showToast(requireContext(), response.body()!!.message)
                        requireActivity().finish()
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

    companion object {
        private const val POLICY_URL =
            "https://webcontrol.webcontrol.cl/manuales/WebControl_Acuerdo_Uso.asp"
        private const val REQUEST_CODE = 1
    }
}