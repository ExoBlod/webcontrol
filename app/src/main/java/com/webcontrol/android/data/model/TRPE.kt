package com.webcontrol.android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TRPE (
        var lugar: String? = "",
        var fechaRetorno: String? = "",
        var checked:Boolean = false
): Parcelable