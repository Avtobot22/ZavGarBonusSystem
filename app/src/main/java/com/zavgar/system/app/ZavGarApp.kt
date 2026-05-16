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
            appModule = module {
                single<Context> { this@ZavGarApp }
            }
        )

    }

}