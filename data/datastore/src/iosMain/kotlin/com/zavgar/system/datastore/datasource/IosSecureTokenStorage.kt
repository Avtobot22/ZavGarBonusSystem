package com.zavgar.system.datastore.datasource

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytes
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRange
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.NSBundle
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class)
internal class IosSecureTokenStorage : SecureTokenStorage {

    private val service: String =
        NSBundle.mainBundle.bundleIdentifier ?: "com.zavgar.system"

    override suspend fun saveAccessToken(token: String) = save(ACCOUNT_ACCESS, token)

    override suspend fun saveRefreshToken(token: String) = save(ACCOUNT_REFRESH, token)

    override suspend fun getAccessToken(): String? = load(ACCOUNT_ACCESS)

    override suspend fun getRefreshToken(): String? = load(ACCOUNT_REFRESH)

    override suspend fun clear() {
        delete(ACCOUNT_ACCESS)
        delete(ACCOUNT_REFRESH)
    }

    private fun save(account: String, token: String) {
        val serviceRef = service.toCFString()
        val accountRef = account.toCFString()
        val dataRef = token.toCFData()
        try {
            val updateQuery = newMutableDictionary()
            CFDictionaryAddValue(updateQuery, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(updateQuery, kSecAttrService, serviceRef)
            CFDictionaryAddValue(updateQuery, kSecAttrAccount, accountRef)

            val updateAttrs = newMutableDictionary()
            CFDictionaryAddValue(updateAttrs, kSecValueData, dataRef)
            CFDictionaryAddValue(updateAttrs, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

            val status = SecItemUpdate(updateQuery, updateAttrs)
            CFRelease(updateQuery)
            CFRelease(updateAttrs)

            if (status == errSecItemNotFound) {
                val addQuery = newMutableDictionary()
                CFDictionaryAddValue(addQuery, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(addQuery, kSecAttrService, serviceRef)
                CFDictionaryAddValue(addQuery, kSecAttrAccount, accountRef)
                CFDictionaryAddValue(addQuery, kSecValueData, dataRef)
                CFDictionaryAddValue(addQuery, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)
                SecItemAdd(addQuery, null)
                CFRelease(addQuery)
            }
        } finally {
            CFRelease(serviceRef)
            CFRelease(accountRef)
            CFRelease(dataRef)
        }
    }

    private fun load(account: String): String? {
        val serviceRef = service.toCFString()
        val accountRef = account.toCFString()
        return memScoped {
            val query = newMutableDictionary()
            CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(query, kSecAttrService, serviceRef)
            CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
            CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

            val resultVar = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, resultVar.ptr)
            CFRelease(query)
            CFRelease(serviceRef)
            CFRelease(accountRef)

            if (status != errSecSuccess) return@memScoped null
            val raw = resultVar.value ?: return@memScoped null
            val data: CFDataRef = raw.reinterpret()
            val text = cfDataToString(data)
            CFRelease(raw)
            text
        }
    }

    private fun delete(account: String) {
        val serviceRef = service.toCFString()
        val accountRef = account.toCFString()
        val query = newMutableDictionary()
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, serviceRef)
        CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
        SecItemDelete(query)
        CFRelease(query)
        CFRelease(serviceRef)
        CFRelease(accountRef)
    }

    private fun newMutableDictionary() = CFDictionaryCreateMutable(
        null,
        0,
        kCFTypeDictionaryKeyCallBacks.ptr,
        kCFTypeDictionaryValueCallBacks.ptr,
    )!!

    private fun String.toCFString(): CFStringRef =
        CFStringCreateWithCString(null, this, kCFStringEncodingUTF8)!!

    private fun String.toCFData(): CFDataRef {
        val bytes = encodeToByteArray()
        return bytes.usePinned { pinned ->
            CFDataCreate(null, pinned.addressOf(0).reinterpret(), bytes.size.convert())!!
        }
    }

    private fun cfDataToString(data: CFDataRef): String {
        val length = CFDataGetLength(data).toInt()
        if (length <= 0) return ""
        val bytes = ByteArray(length)
        val range = cValue<CFRange> {
            location = 0.convert()
            this.length = length.convert()
        }
        bytes.usePinned { pinned ->
            CFDataGetBytes(data, range, pinned.addressOf(0).reinterpret())
        }
        return bytes.decodeToString()
    }

    private companion object {
        const val ACCOUNT_ACCESS = "access_token"
        const val ACCOUNT_REFRESH = "refresh_token"
    }
}
