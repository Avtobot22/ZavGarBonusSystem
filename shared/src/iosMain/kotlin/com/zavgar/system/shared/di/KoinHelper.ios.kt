package com.zavgar.system.shared.di

/**
 * iOS-обёртка над [initKoin] с явным параметром.
 *
 * Kotlin/Native не экспортирует значения параметров по умолчанию в Swift, поэтому для
 * управления mock-сервером с iOS заводится отдельная функция без перегрузок. Вызывается
 * из `iosApp` (`KoinHelperKt.doInitKoinIos(useMockServer:)`).
 *
 * @param useMockServer когда `true`, сетевой слой работает на in-memory Ktor mock без
 *   реального бэкенда (тот же offline-режим, что и на Android).
 */
fun initKoinIos(useMockServer: Boolean = false) {
    initKoin(
        isDebugBuild = isDebugBuildDefault,
        useMockServer = useMockServer,
    )
}
