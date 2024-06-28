package com.webcontrol.android.util

import android.content.Context
import android.preference.PreferenceManager

object PreferenceUtil {
    const val PREF_FINGERPRINT = "pref_fingerprint"
    const val PREF_CAMBIAR_CLAVE = "pref_cambiar_clave"
    const val PREF_RUT = "pref_rut"
    const val PREF_TELEFONO = "pref_telefono"
    const val PREF_EMAIL = "pref_email"
    const val PREF_TUTORIAL_PHOTO = "pref_tutorial_photo"
    const val PREF_UBICACION = "pref_permission"
    const val PREF_GRANT_LOCATION = "pref_grant_location"
    const val PREF_PRIVACY_POLICY = "pref_privacy_policy"
    const val PREF_DELETE_ACCOUNT = "pref_delete_account"

    const val PREF_RBUS_FECHA_IDA = "pref_rb_fecha_ida"
    const val PREF_RBUS_FECHA_VUELTA = "pref_rb_fecha_vuelta"
    const val PREF_RBUS_DESTINO = "pref_rb_destino"
    const val PREF_RBUS_ORIGEN = "pref_rb_origen"
    const val PREF_RBUS_DIVSION = "pref_rb_division"
    const val PREF_RBUS_IDPROG_IDA = "pref_rb_idprog_ida"
    const val PREF_RBUS_IDPROG_VUELTA = "pref_rb_idprog_vuelta"
    const val PREF_RBUS_ASIENTO_IDA = "pref_rb_asiento_ida"
    const val PREF_RBUS_ASIENTO_VUELTA = "pref_rb_asiento_vuelta"
    const val PREF_RBUS_PATENTE_IDA = "pref_rb_patente_ida"
    const val PREF_RBUS_PATENTE_VUELTA = "pref_rb_patente_vuelta"
    @JvmStatic
    fun setBooleanPreference(context: Context?, key: String?, value: Boolean) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @JvmStatic
    fun setStringPreference(context: Context?, key: String?, value: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getBooleanPreference(context: Context?, key: String?, defaultValue: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            pref.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    @JvmStatic
    fun getStringPreference(context: Context?, key: String?): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            pref.getString(key, "")!!
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


}