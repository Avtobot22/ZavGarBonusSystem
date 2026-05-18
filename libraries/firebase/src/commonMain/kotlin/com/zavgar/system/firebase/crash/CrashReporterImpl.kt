package com.zavgar.system.firebase.crash

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.FirebaseCrashlytics
import dev.gitlive.firebase.crashlytics.crashlytics

internal class CrashReporterImpl : CrashReporter {

    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
}
