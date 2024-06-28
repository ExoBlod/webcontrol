package com.webcontrol.android.ui.common

import com.webcontrol.android.data.network.ApiResponseLaPoderosaCredencial
import com.webcontrol.android.util.SafeMediatorLiveData

data class GenericViewStateLaPoderosa<T>(
    var data: ApiResponseLaPoderosaCredencial<T>? = null,
    var isLoading: Boolean = false,
    var error: String? = null
)

fun <T> SafeMediatorLiveData<GenericViewStateLaPoderosa<T>>.update(
    data: ApiResponseLaPoderosaCredencial<T>? = value.data,
    isLoading: Boolean = value.isLoading,
    error: String? = value.error
) {
    value = value.copy(data = data, isLoading = isLoading, error = error)
}