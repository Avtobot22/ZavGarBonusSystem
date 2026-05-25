package com.zavgar.system.datastore.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal class AndroidSecureTokenStorage(
    context: Context,
) : SecureTokenStorage {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override suspend fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS, token).apply()
    }


    override suspend fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH, token).apply()
    }


    override suspend fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS, null)


    override suspend fun getRefreshToken(): String? =
        prefs.getString(KEY_REFRESH, null)


    override suspend fun clear() {
        prefs.edit().clear().apply()
    }


    private companion object {
        const val PREFS_FILE = "zavgar_secure_tokens"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
