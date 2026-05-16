package com.zavgar.system.shared.di

// On Android the flag is passed explicitly by the application module
// (which owns BuildConfig.DEBUG); this default is only a safe fallback.
internal actual val isDebugBuildDefault: Boolean = false
