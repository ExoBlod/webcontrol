package com.webcontrol.angloamerican.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class StateUpdateDoc(val state:String){
    APROBADO("SI"),
    EN_REVISION("NO"),
    RECHAZADO("RE")
}

inline fun FragmentManager.doTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun Fragment.addFragment(fragmentManager: FragmentManager, containerId: Int, fragment: Fragment, tag: String) {
    fragmentManager.doTransaction { add(containerId, fragment, tag) }
}

fun Fragment.replaceFragment(fragmentManager: FragmentManager, containerId: Int, fragment: Fragment, tag: String) {
    fragmentManager.doTransaction { replace(containerId, fragment, tag) }
}

fun Fragment.removeFragment(fragmentManager: FragmentManager, fragment: Fragment) {
    fragmentManager.doTransaction { remove(fragment) }
}

/**
 * Helper functions to simplify permission checks/requests.
 */
fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun String.correctFormatDate():String{
    var tmpString=""
    val year = this.substring(0,4)
    val month = this.substring(4,6)
    val day = this.substring(6,8)
    tmpString = "$day / $month / $year"
    return tmpString
}

fun String.correctState():String{
    return when (this){
        StateUpdateDoc.APROBADO.state -> "APROBADO"
        StateUpdateDoc.EN_REVISION.state -> "EN REVISIÓN"
        StateUpdateDoc.RECHAZADO.state -> "RECHAZADO"
        else -> {"NOT FOUND"}
    }
}

fun ViewModel.launch(
    context: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(block = block, context = context)
}

fun convertirFecha(fechaAAAAMMDD: String): String {
    if (fechaAAAAMMDD.length != 8) {
        return "" // La fecha no tiene el formato esperado
    }

    val año = fechaAAAAMMDD.substring(0, 4)
    val mes = fechaAAAAMMDD.substring(4, 6)
    val día = fechaAAAAMMDD.substring(6, 8)

    // Formatea la fecha como "dd/mm/yyyy"
    return "$día/$mes/$año"
}

fun String.getExtension(): String? {
    val ultimoPunto = this.lastIndexOf('.')
    return if (ultimoPunto >= 0 && ultimoPunto < this.length - 1) {
        this.substring(ultimoPunto + 1)
    } else {
        null // No se encontró una extensión o el nombre de archivo está vacío
    }
}