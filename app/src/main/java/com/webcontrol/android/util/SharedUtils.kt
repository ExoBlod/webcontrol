package com.webcontrol.android.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.widgets.MessageView
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_ASIENTO_IDA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_ASIENTO_VUELTA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_IDPROG_IDA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_IDPROG_VUELTA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_PATENTE_IDA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_PATENTE_VUELTA
import com.webcontrol.angloamerican.data.COURSE_ID
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


object SharedUtils {
    @JvmStatic
    fun setUsuarioId(context: Context?, usuarioId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("USER_ID", usuarioId)
        editor.apply()
    }

    @JvmStatic
    fun getWorkerQrId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("RUT_QR", "")!!
    }

    @JvmStatic
    fun setWorkerQrId(context: Context?, workerId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("RUT_QR", workerId)
        editor.apply()
    }

    @JvmStatic
    fun getUsuarioId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("USER_ID", "")!!
    }

    @JvmStatic
    fun setUsuario(context: Context?, usuario: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("USER_NAME", usuario)
        editor.apply()
    }

    @JvmStatic
    fun getUsuario(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("USER_NAME", "")!!
    }

    @JvmStatic
    fun setToken(context: Context?, usuario: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("USER_TOKEN", usuario)
        editor.apply()
    }

    @JvmStatic
    fun setTelefonoCovidCDL(context: Context?, telefono: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("COV_PHONE", telefono)
        editor.apply()
    }

    @JvmStatic
    fun getTelefonoCovidCDL(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("COV_PHONE", "")!!
    }

    fun setTelefonoCovidGF(context: Context?, telefono: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("COV_PHONE_GF", telefono)
        editor.apply()
    }

    fun getTelefonoCovidGF(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("COV_PHONE_GF", "")!!
    }

    @JvmStatic
    fun setTelefonoCovidKRS(context: Context?, telefono: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("COV_PHONE", telefono)
        editor.apply()
    }

    @JvmStatic
    fun getTelefonoCovidKRS(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("COV_PHONE", "")!!
    }

    @JvmStatic
    fun setLocationCoordinates(context: Context?, coordinates: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("LOC_CDT", coordinates)
        editor.apply()
    }

    @JvmStatic
    fun getLocationCoordinates(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("LOC_CDT", "")!!
    }

    @JvmStatic
    fun getToken(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("USER_TOKEN", "")!!
    }

    fun setTokenAuthorization(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_AUTHORIZATION", token)
        editor.apply()
    }

    fun getTokenAuthorizationMc(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_AUTHORIZATION_MC", "")!!
    }

    fun getTokenAuthorizationPHC(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_AUTHORIZATION_PHC", "")!!
    }

    fun setTokenAuthorizationPHC(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_AUTHORIZATION_PHC", token)
        editor.apply()
    }

    fun setTokenAuthorizationMc(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_AUTHORIZATION_MC", token)
        editor.apply()
    }

    fun getTokenAuthorization(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_AUTHORIZATION", "")!!
    }

    fun setTokenKS(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_KS_V3", "Bearer $token")
        editor.apply()
    }

    fun getTokenKS(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_KS_V3", "")!!
    }

    fun setTokenYamana(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_KS", "Bearer $token")
        editor.apply()
    }

    fun getTokenYamana(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_KS", "")!!
    }

    fun setTokenBambas(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_BAMBAS", "Bearer $token")
        editor.apply()
    }

    fun getTokenBambas(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_BAMBAS", "")!!
    }

    fun setTokenCaserones(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_CASERONES", "Bearer $token")
        editor.apply()
    }

    fun getTokenCaserones(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_CASERONES", "")!!
    }

    fun setTokenEA(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_EA", "Bearer $token")
        editor.apply()
    }

    fun getTokenEA(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_EA", "")!!
    }

    fun setTokenAnta(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_ANTA", "Bearer $token")
        editor.apply()
    }

    fun getTokenAnta(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_ANTA", "")!!
    }

    fun setTokenKinross(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_KINROSS", "Bearer $token")
        editor.apply()
    }

    fun getTokenKinross(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_KINROSS", "")!!
    }

    fun setTokenSgmc(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_SGMC", "Bearer $token")
        editor.apply()
    }

    fun getTokenSgmc(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_SGMC", "")!!
    }

    fun setTokenGoldfields(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_GOLDFIELDS", "Bearer $token")
        editor.apply()
    }

    fun getTokenGoldfields(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_GOLDFIELDS", "")!!
    }

    fun setTokenPHC(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_PHC", "Bearer $token")
        editor.apply()
    }

    fun getTokenPHC(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_PHC", "")!!
    }

    @JvmStatic
    fun setRoomTwilio(context: Context?, room: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("ROOM_TWILIO", room)
        editor.apply()
    }

    @JvmStatic
    fun getRoomTwilio(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("ROOM_TWILIO", "")!!
    }

    @JvmStatic
    fun needUpdate(context: Context?, update: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("USER_UPDATE", update)
        editor.apply()
    }

    @JvmStatic
    fun setSession(context: Context?, session: Boolean?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("pref_session", session!!)
        editor.apply()
    }

    @JvmStatic
    fun getSession(context: Context?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("pref_session", false)
    }

    @JvmStatic
    fun setFirstRun(context: Context?, value: Boolean?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("pref_first_run", value!!)
        editor.apply()
    }

    @JvmStatic
    fun isFirtRun(context: Context?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("pref_first_run", true)
    }

    @JvmStatic
    fun setFirstLogin(context: Context?, value: Boolean?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("pref_first_login", value!!)
        editor.apply()
    }

    @JvmStatic
    fun isFirtLogin(context: Context?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("pref_first_login", true)
    }

    fun set_uid(context: Context?, uid: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("UID", uid)
        editor.apply()
    }

    @JvmStatic
    fun get_email(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_email", "")!!
    }

    @JvmStatic
    fun set_email(context: Context?, email: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_email", email)
        editor.apply()
    }

    @JvmStatic
    fun get_zip_code(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_zipcode", "")!!
    }

    @JvmStatic
    fun set_zip_code(context: Context?, zip_code: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_zipcode", zip_code)
        editor.apply()
    }

    @JvmStatic
    fun get_telefono(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_telefono", "")!!
    }

    @JvmStatic
    fun set_telefono(context: Context?, zip_code: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_telefono", zip_code)
        editor.apply()
    }

    fun get_uid(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("UID", "DEFAULT")!!
    }

    fun setBarrickDivisionPref(context: Context?, divisionName: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("BARRICK_DIVISION_PREF", divisionName)
        editor.apply()
    }

    fun getBarrickDivisionPref(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("BARRICK_DIVISION_PREF", "")!!
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    @JvmStatic
    fun getIMEI(context: Context): String {
        val phonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var id: String? = null
        try {
            id = phonyManager.deviceId
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        if (id == null) {
            id = get_uid(context)
            if (id == "DEFAULT") {
                id = UUID.randomUUID().toString()
                set_uid(context, id)
            }
        }
        return id
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    @JvmStatic
    fun getIMEIPHC(context: Context): Int {
        val phonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var id: String? = null
        try {
            id = phonyManager.deviceId
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        if (id == null) {
            id = get_uid(context)
            if (id == "DEFAULT") {
                id = UUID.randomUUID().toString()
                set_uid(context, id)
            }
        }
        return id?.hashCode() ?: 0
    }

    @JvmStatic
    val wCDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }
    fun getStringFromDate(date: Date, pattern: String = "yyyyMMdd"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    @JvmStatic
    val date: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }

    @JvmStatic
    val time: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }

    @JvmStatic
    fun getNiceDate(inputDate: String?): String {
        val inputFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        var result = ""
        try {
            val date = inputFormat.parse(inputDate)
            result = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getWCDateformat(inputDate: String?): String {
        val inputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val outputFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        var result = ""
        try {
            val date = inputFormat.parse(inputDate)
            result = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getCustomDateFormat(inputDate: String, inputStr: String, outputStr: String): String {
        val inputFormat: DateFormat = SimpleDateFormat(inputStr, Locale.US)
        val outputFormat: DateFormat = SimpleDateFormat(outputStr, Locale.US)
        var result = ""
        try {
            val date = inputFormat.parse(inputDate)
            result = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    @JvmStatic
    fun showToast(context: Context, message: String? = "Desconocido") {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showToast(context: Context?, message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showMessage(messageView: MessageView, pic: Int, msg: String?) {
        messageView.setDrawable(pic)
        messageView.setText(msg)
        messageView.visibility = View.VISIBLE
    }

    @JvmStatic
    fun setTermsandConditions(context: Context?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("pref_agreed", true)
        editor.apply()
    }

    @JvmStatic
    fun getTermsAndCondition(context: Context?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("pref_agreed", false)
    }

    @JvmStatic
    fun rememberUsernameChecked(context: Context?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("pref_remember_username_check", false)
    }

    @JvmStatic
    fun rememberUsernameChecked(context: Context?, checked: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("pref_remember_username_check", checked)
        editor.apply()
    }

    @JvmStatic
    fun stringToDate(
        fecha: String? = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()).toString(),
        Hora: String? = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date()).toString()
    ): Date {
        var d_fecha = Date()
        try {
            val formatter: SimpleDateFormat
            formatter = SimpleDateFormat("yyyyMMdd HH:mm")
            d_fecha = formatter.parse("$fecha $Hora")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return d_fecha
    }

    fun formatDateTime(fecha: String, Hora: String): String {
        var fechaRetorno = ""
        val Mes = arrayOf(
            "ene",
            "feb",
            "mar",
            "abri",
            "may",
            "jun",
            "jul",
            "ago",
            "sep",
            "oct",
            "nov",
            "dic"
        )
        val currentDate = Calendar.getInstance()
        val fechaInicial = Calendar.getInstance()
        try {
            val formatter: SimpleDateFormat
            formatter = SimpleDateFormat("yyyyMMdd HH:mm")
            val fechaIni = formatter.parse("$fecha $Hora")
            fechaInicial.time = fechaIni
            fechaRetorno = when (fechaInicial.compareTo(currentDate)) {
                1 -> {
                    SimpleDateFormat("HH:mm").format(fechaInicial.time)
                }

                0 -> SimpleDateFormat("HH:mm").parse(Hora).toString()
                -1 -> if (fechaInicial[Calendar.DAY_OF_MONTH] == currentDate[Calendar.DAY_OF_MONTH] && fechaInicial[Calendar.MONTH] == currentDate[Calendar.MONTH] && fechaInicial[Calendar.YEAR] == currentDate[Calendar.YEAR]) SimpleDateFormat(
                    "HH:mm"
                ).format(fechaIni.time) else if (fechaInicial[Calendar.MONTH] == currentDate[Calendar.MONTH]
                    && fechaInicial[Calendar.YEAR] == currentDate[Calendar.YEAR]
                ) fechaInicial[Calendar.DAY_OF_MONTH].toString() + " " + Mes[currentDate[Calendar.MONTH]] else SimpleDateFormat(
                    "yyyy/MM/dd"
                ).format(fechaInicial.time)

                else -> SimpleDateFormat("yyyy/MM/dd").format(fechaInicial.time)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fechaRetorno
    }

    fun getDayCount(fecha1: String, fecha2: String, format: String): Long? {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.US)
        val fecha1Format = dateFormat.parse(fecha1)
        val fecha2Format = dateFormat.parse(fecha2)
        return if (fecha1Format != null && fecha2Format != null) {
            val diff = fecha1Format.time - fecha2Format.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).absoluteValue
        } else
            null
    }

    fun compareDays(fecha1: String, fecha2: String, format: String): Int? {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.US)
        val fecha1Format = dateFormat.parse(fecha1)
        val fecha2Format = dateFormat.parse(fecha2)
        return if (fecha1Format != null && fecha2Format != null)
            fecha1Format.compareTo(fecha2Format)
        else null
    }

    fun parseStringDate(date: String, inPattern: String, outPattern: String): String? {
        return try {
            val originDate = SimpleDateFormat(inPattern, Locale.getDefault()).parse(date)
            SimpleDateFormat(outPattern, Locale.getDefault()).format(originDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun fechaPretty(fecha: String, hora: String): String {
        return formatDateTime(fecha, hora)
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    @JvmStatic
    fun bitMapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return outputStream.toByteArray()
    }

    @JvmStatic
    fun getRandomMaterialColor(context: Context, typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId =
            context.resources.getIdentifier("mdcolor_$typeColor", "array", context.packageName)
        if (arrayId != 0) {
            val colors = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    @JvmStatic
    fun getOSTCheckList(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_ostchecklist", "")!!
    }

    @JvmStatic
    fun setOSTCheckList(context: Context?, ost: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_ostchecklist", ost)
        editor.apply()
    }

    @JvmStatic
    fun getCompanyCheckList(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_companychecklist", "")!!
    }

    @JvmStatic
    fun setCompanyCheckList(context: Context?, ost: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_companychecklist", ost)
        editor.apply()
    }

    fun setIdCompany(context: Context?, id: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_idcompany", id)
        editor.apply()
    }

    fun getIdCompany(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_idcompany", "")!!
    }

    @JvmStatic
    fun getAntaCompany(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_antacompany", "")!!
    }

    @JvmStatic
    fun setAntaCompany(context: Context?, ost: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_antacompany", ost)
        editor.apply()
    }

    @JvmStatic
    fun getWorkerDivision(context: Context?): String? {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("pref_division", "")
    }

    @JvmStatic
    fun setWorkerDivision(context: Context?, ost: String?) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("pref_division", ost)
        editor.apply()
    }


    @JvmStatic
    fun toTitleCase(str: String?): String? {
        if (str == null) {
            return null
        }
        var space = true
        val builder = StringBuilder(str)
        val len = builder.length
        for (i in 0 until len) {
            val c = builder[i]
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    space = false
                }
            } else if (Character.isWhitespace(c)) {
                space = true
            } else {
                builder.setCharAt(i, Character.toLowerCase(c))
            }
        }
        return builder.toString()
    }

    @JvmStatic
    fun showLoader(context: Context?, message: String?) {
        ProgressLoadingJIGB.startLoadingJIGB(context, R.raw.loaddinglottie, message, 0, 500, 200)
    }

    @JvmStatic
    fun dismissLoader(context: Context?) {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    fun showSimpleNotification(context: Context, title: String?, message: String?) {
        val CHANNEL_ID = "channel_01"
        // Get an instance of the Notification manager
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)
        }

        // Create an explicit content Intent that starts the main Activity.
        val notificationIntent = Intent(context, MainActivity::class.java)

        // Construct a task stack.
        val stackBuilder = TaskStackBuilder.create(context)

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity::class.java)

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent)

        // Get a PendingIntent containing the entire back stack.
        val notificationPendingIntent: PendingIntent?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        }

        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher
                )
            )
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(notificationPendingIntent)

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true)

        // Issue the notification
        mNotificationManager.notify(0, builder.build())
    }

    @JvmStatic
    fun FormatRut(rut: String): String {
        var rut = rut
        return if (rut.length <= 8) rut else {
            var cont = 0
            var format: String
            rut = rut.replace(".", "")
            rut = rut.replace("-", "")
            format = "-" + rut.substring(rut.length - 1)
            for (i in rut.length - 2 downTo 0) {
                format = rut.substring(i, i + 1) + format
                cont++
                if (cont == 3 && i != 0) {
                    format = ".$format"
                    cont = 0
                }
            }
            format
        }
    }

    @JvmStatic
    fun FormatRutCharacters(rut: String): String {
        var rut = rut
        return if (rut.length <= 8) rut else {
            rut = rut.replace(".", "")
            rut = rut.replace("-", "")
            return rut
        }
    }

    fun getUserAge(fecNac: String): Int {
        val anioNac = fecNac.substring(0, 4).toInt()
        val fecActual = wCDate
        var edad = fecActual.substring(0, 4).toInt() - anioNac
        if (fecNac.substring(4, 6)
                .compareTo(fecActual.substring(4, 6)) > 0
        ) edad -= 1 else if (fecNac.substring(4, 6).compareTo(fecActual.substring(4, 6)) == 0) {
            if (fecNac.substring(6).compareTo(fecActual.substring(6)) > 0) edad -= 1
        }
        return Math.max(edad, 0)
    }

    @JvmStatic
    fun setTmzNow(context: Context?, usuarioId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TMZ_NOW_DATE", usuarioId)
        editor.apply()
    }

    @JvmStatic
    fun getTmzNow(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TMZ_NOW_DATE", "")!!
    }

    @JvmStatic
    fun setWorkerId(context: Context?, workerId: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("WORKER_ID", workerId)
        editor.apply()
    }

    @JvmStatic
    fun getWorkerId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("WORKER_ID", "")!!
    }
    fun setTokenPuCobre(context: Context?, token: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("TOKEN_PUCOBRE", "Bearer $token")
        editor.apply()
    }

    fun getTokenPuCobre(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_PUCOBRE", "")!!
    }


    @JvmStatic
    fun setRequestLocationStatus(context: Context, status: Boolean) {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("REQUEST_LOCATION_STATUS", status)
        editor.apply()
    }

    @JvmStatic
    fun getRequestLocationStatus(context: Context): Boolean {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("REQUEST_LOCATION_STATUS", false)
    }

    fun showDialog(
        context: Context,
        title: String,
        content: String,
        positiveText: String,
        positiveAction: MaterialDialog.SingleButtonCallback,
        negativeText: String? = null,
        negativeAction: MaterialDialog.SingleButtonCallback? = null,
        dismiss: Boolean = true
    ) {
        val dialogBuilder =
            MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .autoDismiss(dismiss)
                .positiveText(positiveText)
                .onPositive(positiveAction)
        negativeText?.let {
            dialogBuilder.negativeText(it)
            negativeAction?.let { callback ->
                dialogBuilder.onNegative(callback)
            }
        }
        dialogBuilder.show()

    }

    fun getRutString(dato: String): String {
        var rut: String
        return try {
            if (dato.length > 10) {
                if (dato.contains("http")) {
                    rut = dato.substring(dato.indexOf("RUN=") + 4, dato.indexOf("&type"))
                    rut = rut.replace("-", "").replace("&", "").trim { it <= ' ' }
                } else {
                    val firstChar = dato.substring(0, 1)
                    rut = if (firstChar == "1" || firstChar == "2" || firstChar == "3") {
                        dato.substring(0, 9)
                    } else {
                        dato.substring(0, 8)
                    }
                }
            } else {
                rut = dato
            }
            rut
        } catch (e: Exception) {
            "ERROR"
        }
    }

    @JvmStatic
    fun getTypeReservaBusFromString(type: String): TypeReservaBus {

        if (TypeReservaBus.IDA.valor == type)
            return TypeReservaBus.IDA
        if (TypeReservaBus.VUELTA.valor == type)
            return TypeReservaBus.VUELTA
        if (TypeReservaBus.IDAYVUELTA.valor == type)
            return TypeReservaBus.IDAYVUELTA
        return TypeReservaBus.IDA
    }

    @JvmStatic
    fun setBusIda(context: Context?, IdProg: Long) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putLong(PREF_RBUS_IDPROG_IDA, IdProg)
        editor.apply()
    }

    @JvmStatic
    fun getBusIda(context: Context?): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getLong(PREF_RBUS_IDPROG_IDA, -1L)
    }

    @JvmStatic
    fun setBusVuelta(context: Context?, IdProg: Long) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putLong(PREF_RBUS_IDPROG_VUELTA, IdProg)
        editor.apply()
    }

    @JvmStatic
    fun getBusVuelta(context: Context?): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getLong(PREF_RBUS_IDPROG_VUELTA, -1L)
    }

    @JvmStatic
    fun setBusAsientoVuelta(context: Context?, nroSeat: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putInt(PREF_RBUS_ASIENTO_VUELTA, nroSeat)
        editor.apply()
    }

    @JvmStatic
    fun getBusAsientoVuelta(context: Context?): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(PREF_RBUS_ASIENTO_VUELTA, -1)
    }

    @JvmStatic
    fun setBusAsientoIda(context: Context?, nroSeat: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putInt(PREF_RBUS_ASIENTO_IDA, nroSeat)
        editor.apply()
    }

    @JvmStatic
    fun getBusAsientoIda(context: Context?): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(PREF_RBUS_ASIENTO_IDA, -1)
    }

    @JvmStatic
    fun setBusPatenteIda(context: Context?, patente: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(PREF_RBUS_PATENTE_IDA, patente)
        editor.apply()
    }

    @JvmStatic
    fun getBusPatenteIda(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(PREF_RBUS_PATENTE_IDA, "")!!
    }

    @JvmStatic
    fun setBusPatenteVuelta(context: Context?, patente: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(PREF_RBUS_PATENTE_VUELTA, patente)
        editor.apply()
    }

    @JvmStatic
    fun getBusPatenteVuelta(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(PREF_RBUS_PATENTE_VUELTA, "")!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasFingerPrintSupport(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    @JvmStatic
    fun setCourseId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(COURSE_ID, "")!!
    }


    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    fun generateQR(source: String, height: Int = 1024, width: Int = 1024): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
    }

}