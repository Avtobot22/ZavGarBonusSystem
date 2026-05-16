package com.zavgar.system.shared.di

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

internal actual val isDebugBuildDefault: Boolean
    @OptIn(ExperimentalNativeApi::class)
    get() = Platform.isDebugBinary
