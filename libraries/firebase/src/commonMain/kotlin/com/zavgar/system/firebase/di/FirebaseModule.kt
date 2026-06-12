package com.zavgar.system.firebase.di

import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.config.AppConfig
import com.zavgar.system.config.IS_DEBUG_BUILD
import com.zavgar.system.firebase.analytics.AnalyticsTrackerImpl
import com.zavgar.system.firebase.analytics.analyticsPlatform
import com.zavgar.system.firebase.config.RemoteConfigServiceImpl
import com.zavgar.system.firebase.crash.CrashReporter
import com.zavgar.system.firebase.crash.CrashReporterImpl
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val firebaseModule = module {

    single {
        RemoteConfigServiceImpl(isDebugBuild = get(named(IS_DEBUG_BUILD)))
    } bind AppConfig::class

    single<CrashReporter> { CrashReporterImpl() }

    single<AnalyticsTracker> { AnalyticsTrackerImpl(platform = analyticsPlatform) }
}
