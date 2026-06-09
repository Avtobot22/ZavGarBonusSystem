package com.zavgar.system.app

import android.app.Application
import android.content.Context
import com.zavgar.system.shared.di.initKoin
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.dsl.module

class ZavGarApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

        initKoin(
            isDebugBuild = BuildConfig.DEBUG,
            // Управляется build-полем USE_MOCK_SERVER (см. app/build.gradle.kts):
            // true -> приложение работает на заготовленных ответах без реального бэкенда.
            useMockServer = BuildConfig.USE_MOCK_SERVER,
            appModule = module {
                single<Context> { this@ZavGarApp }
            },
        )
    }
}
