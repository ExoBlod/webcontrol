package com.webcontrol.android.ui.common

import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ApiResponseBambasCredential
import com.webcontrol.android.util.SafeMediatorLiveData


data class GenericViewStateBambas<T>(
    var data: ApiResponseBambasCredential<T>? = null,
    var isLoading: Boolean = false,
    //val error: String? = ". "
    var error:String?=null
)

fun <T> SafeMediatorLiveData<GenericViewStateBambas<T>>.update(
    data: ApiResponseBambasCredential<T>? = value.data,
    isLoading: Boolean = value.isLoading,
    error: String? = value.error
) {
    value = value.copy(data = data, isLoading = isLoading, error = error)
}