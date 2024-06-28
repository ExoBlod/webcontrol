package com.webcontrol.android.ui.common

import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.SafeMediatorLiveData

data class GenericViewState<T>(
        val data: ApiResponseAnglo<T>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)

fun <T> SafeMediatorLiveData<GenericViewState<T>>.update(
        data: ApiResponseAnglo<T>? = value.data,
        isLoading: Boolean = value.isLoading,
        error: String? = value.error
) {
    value = value.copy(data = data, isLoading = isLoading, error = error)
}