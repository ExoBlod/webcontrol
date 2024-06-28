package com.webcontrol.android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkerAptoDJ(
    val rut: String?,
    val fecha: String?
): Parcelable