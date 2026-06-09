package com.zavgar.system.networkmock.di

import com.zavgar.system.network.di.NETWORK_ENGINE
import com.zavgar.system.networkmock.createMockEngine
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль mock-сервера.
 *
 * Подмешивается в граф вместо реального транспорта (через `initKoin(useMockServer = true)`):
 * предоставляет [HttpClientEngine] под квалификатором [NETWORK_ENGINE], который `networkModule`
 * подхватывает и собирает на нём оба `HttpClient`. Реальные сервисы и репозитории не меняются —
 * приложение работает на заготовленных ответах без живого бэкенда (Android и iOS).
 */
val mockNetworkModule = module {
    single<HttpClientEngine>(named(NETWORK_ENGINE)) { createMockEngine() }
}
