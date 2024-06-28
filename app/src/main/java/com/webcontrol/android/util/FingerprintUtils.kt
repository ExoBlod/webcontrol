package com.webcontrol.android.util

import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.spec.InvalidKeySpecException
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

@RequiresApi(Build.VERSION_CODES.M)
object FingerprintUtils {
    private const val KEY_ALIAS = "FINGERPRINT_KEY_PAIR_ALIAS"
    private const val KEY_STORE = "AndroidKeyStore"
    private var sKeyStore: KeyStore? = null
    private var sKeyPairGenerator: KeyPairGenerator? = null
    private var sCipher: Cipher? = null
    @JvmStatic
    fun checkSensorState(context: Context): Boolean {
        val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        return if (fingerprintManager.isHardwareDetected) {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.isKeyguardSecure && fingerprintManager.hasEnrolledFingerprints()
        } else false
    }

    @JvmStatic
    fun encryptString(string: String): String? {
        try {
            if (initKeyStore() && initCipher() && initKey() && initCipherMode(Cipher.ENCRYPT_MODE)) {
                val bytes = sCipher!!.doFinal(string.toByteArray())
                return Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    fun decryptString(string: String?, cipher: Cipher): String? {
        try {
            val bytes = Base64.decode(string, Base64.NO_WRAP)
            return String(cipher.doFinal(bytes))
        } catch (exception: IllegalBlockSizeException) {
            exception.printStackTrace()
        } catch (exception: BadPaddingException) {
            exception.printStackTrace()
        }
        return null
    }

    private fun initKeyStore(): Boolean {
        try {
            sKeyStore = KeyStore.getInstance(KEY_STORE)
            sKeyStore!!.load(null)
            return true
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        return false
    }

    private fun initCipher(): Boolean {
        try {
            sCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            return true
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return false
    }

    private fun initCipherMode(mode: Int): Boolean {
        try {
            sKeyStore!!.load(null)
            when (mode) {
                Cipher.ENCRYPT_MODE -> {
                    val key = sKeyStore!!.getCertificate(KEY_ALIAS).publicKey
                    val unrestricted = KeyFactory.getInstance(key.algorithm).generatePublic(X509EncodedKeySpec(key.encoded))
                    val spec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
                    sCipher!!.init(mode, unrestricted, spec)
                }
                Cipher.DECRYPT_MODE -> {
                    val privateKey = sKeyStore!!.getKey(KEY_ALIAS, null) as PrivateKey
                    try {
                        sCipher!!.init(mode, privateKey)
                    } catch (e: KeyPermanentlyInvalidatedException) {
                        sKeyStore!!.deleteEntry(KEY_ALIAS)
                        initKey()
                        sCipher!!.init(mode, privateKey)
                    }
                }
                else -> return false
            }
            return true
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }
        return false
    }

    private fun initKey(): Boolean {
        try {
            return sKeyStore!!.containsAlias(KEY_ALIAS) || generateNewKey()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
        return false
    }

    private fun generateNewKey(): Boolean {
        if (initKeyGenerator()) {
            try {
                sKeyPairGenerator!!.initialize(KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .setUserAuthenticationRequired(true)
                        .build())
                sKeyPairGenerator!!.generateKeyPair()
                return true
            } catch (e: InvalidAlgorithmParameterException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun initKeyGenerator(): Boolean {
        try {
            sKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE)
            return true
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    val cryptoObject: FingerprintManager.CryptoObject?
        get() = if (initKeyStore() && initCipher() && initKey() && initCipherMode(Cipher.DECRYPT_MODE)) {
            FingerprintManager.CryptoObject(sCipher!!)
        } else null
}