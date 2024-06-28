package com.webcontrol.android.util

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import com.webcontrol.android.data.IAuthenticateListener
import com.webcontrol.android.ui.login.LoginActivity

@TargetApi(23)
class FingerprintHandler(private val mContext: Context, private val mSharedPreferences: SharedPreferences, private val mListener: IAuthenticateListener) : FingerprintManager.AuthenticationCallback() {
    private val mCancellationSignal: CancellationSignal?
    fun startAuth(fingerprintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?) {
        fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show()
        mListener.onAuthenticationError()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        Log.d("Fingerprint", helpString.toString())
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        val cipher = result.cryptoObject.cipher
        val encoded = mSharedPreferences.getString(LoginActivity.KEY_PASSWORD, null)
        var decoded: String? = "webcontrol"
        if (encoded != null) {
            decoded = FingerprintUtils.decryptString(encoded, cipher)
        }
        if (decoded != null) {
            mListener.onAuthenticate(decoded)
        } else {
            mListener.onAuthenticationFailed()
        }
    }

    override fun onAuthenticationFailed() {
        mListener.onAuthenticationFailed()
    }

    fun cancel() {
        mCancellationSignal?.cancel()
    }

    init {
        mCancellationSignal = CancellationSignal()
    }
}