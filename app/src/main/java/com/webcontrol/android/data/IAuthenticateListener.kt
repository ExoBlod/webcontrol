package com.webcontrol.android.data

interface IAuthenticateListener {
    fun onAuthenticate(decryptPassword: String)
    fun onAuthenticationFailed()
    fun onAuthenticationError()
}