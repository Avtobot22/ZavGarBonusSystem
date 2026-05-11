package com.zavgar.system.datastore.datasource

/**
 * Platform-secure storage for authentication tokens.
 *
 * Android: backed by EncryptedSharedPreferences + Android Keystore MasterKey.
 * iOS: backed by Keychain Services (kSecClassGenericPassword).
 */
internal interface SecureTokenStorage {

    suspend fun saveAccessToken(token: String)

    suspend fun saveRefreshToken(token: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun clear()
}
