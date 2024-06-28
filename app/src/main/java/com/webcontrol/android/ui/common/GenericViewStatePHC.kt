package com.webcontrol.android.ui.common

import com.webcontrol.android.data.network.ApiResponsePHC
import com.webcontrol.android.util.SafeMediatorLiveData

data class GenericViewStatePHC<T>(
    var data: ApiResponsePHC<T>? = null,
    var isLoading: Boolean = false,
    var error: String? = null
)

fun <T> SafeMediatorLiveData<GenericViewStatePHC<T>>.update(
    data: ApiResponsePHC<T>? = value.data,
    isLoading: Boolean = value.isLoading,
    error: String? = value.error
) {
    value = value.copy(data = data, isLoading = isLoading, error = error)
}