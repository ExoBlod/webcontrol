package com.webcontrol.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.core.R
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

object SharedUtils {
    @JvmStatic
    fun setUsuarioId(context: Context?, usuarioId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("USER_ID", usuarioId)
        editor.apply()
    }

    @JvmStatic
    fun getUsuarioId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("USER_ID", "")!!
    }

    @JvmStatic
    fun getWorkerQrId(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("RUT_QR", "")!!
    }

    @JvmStatic
    fun setRutQr(context: Context?, usuarioId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("RUT_QR", usuarioId)
        editor.apply()
    }

    @JvmStatic
    fun getRutQr(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("RUT_QR", "")!!
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

    fun getTokenPucobre(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_PUCOBRE", "")!!
    }
    fun getTokenAngloamerican(context: Context?): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("TOKEN_ANGLO", "")!!
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

    @JvmStatic
    val wCDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val date = Date()
            return dateFormat.format(date)
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
        inputDate?.let {
            try {
                val date = inputFormat.parse(it)
                result = outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    @JvmStatic
    fun setIdCompany(context: Context?, id: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("pref_idcompany", id)
        editor.apply()
    }

    @JvmStatic
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
    fun FormatRutPucobre(rut: String): String {
        var rut = rut
        return if (rut.length <= 7) rut else {
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

    @JvmStatic
    fun setIdExam(context: Context?, idExamen: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putInt("ID_EXAMEN", idExamen)
        editor.apply()
    }

    @JvmStatic
    fun setIdProgram(context: Context?, idProgram: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putInt("ID_PROGRAM", idProgram)
        editor.apply()
    }
    @JvmStatic
    fun setIdCourse(context: Context?, idCourse: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putInt("ID_COURSE", idCourse)
        editor.apply()
    }
    @JvmStatic
    fun setResultExam(context: Context?, resultExam: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("RESULT_EXAM", resultExam)
        editor.apply()
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
    fun showLoader(context: Context?, message: String?) {
        ProgressLoadingJIGB.startLoadingJIGB(context, R.raw.loaddinglottie, message, 1000, 500, 200)
    }
    @JvmStatic
    fun dismissLoader(context: Context?) {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }
}