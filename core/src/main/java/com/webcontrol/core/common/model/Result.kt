package com.webcontrol.core.common.model

sealed class Result<out T> {}
data class Loading(val isLoading: Boolean) : Result<Nothing>()
data class Success<out T>(val data: T) : Result<T>()
data class Error(val error: String) : Result<Nothing>()