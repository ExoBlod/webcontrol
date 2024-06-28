package com.webcontrol.core.common.model

sealed class Resource<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(isLoading: Boolean = false) : Resource<T>(isLoading = isLoading)
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String) : Resource<T>(message = message)
}