package com.zavgar.system.config

/**
 * Имя Koin-квалификатора для флага debug-сборки.
 *
 * Значение предоставляется в `shared` при инициализации Koin и читается потребителями
 * (сетевой слой, конфиг) через `get(named(IS_DEBUG_BUILD))`. Живёт в контракт-модуле,
 * чтобы потребители не зависели от конкретной реализации конфигурации (`:libraries:firebase`).
 */
const val IS_DEBUG_BUILD: String = "isDebugBuild"
