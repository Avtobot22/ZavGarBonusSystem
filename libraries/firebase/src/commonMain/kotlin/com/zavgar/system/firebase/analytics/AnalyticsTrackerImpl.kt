package com.zavgar.system.firebase.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics

internal class AnalyticsTrackerImpl(
    platform: String,
) : AnalyticsTracker {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    init {
        analytics.setAnalyticsCollectionEnabled(true)
        analytics.setDefaultEventParameters(mapOf("platform" to platform))
    }

    override fun log(event: AnalyticsEvent) {
        analytics.logEvent(event.name, event.params.normalized())
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        val params = buildMap<String, Any> {
            put(PARAM_SCREEN_NAME, screenName)
            screenClass?.let { put(PARAM_SCREEN_CLASS, it) }
        }
        analytics.logEvent(EVENT_SCREEN_VIEW, params)
    }

    override fun setUserId(userId: String?) {
        analytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String?) {
        analytics.setUserProperty(key, value ?: "")
    }

    override fun clearUser() {
        analytics.setUserId(null)
        analytics.resetAnalyticsData()
    }

    /**
     * Firebase-параметры принимают только String / Long / Double.
     * Int → Long, Float → Double, Boolean → String; null отбрасываем.
     */
    private fun Map<String, Any?>.normalized(): Map<String, Any> =
        buildMap {
            this@normalized.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Int -> put(key, value.toLong())
                    is Long -> put(key, value)
                    is Double -> put(key, value)
                    is Float -> put(key, value.toDouble())
                    is Boolean -> put(key, value.toString())
                    null -> Unit
                    else -> put(key, value.toString())
                }
            }
        }

    private companion object {
        const val EVENT_SCREEN_VIEW = "screen_view"
        const val PARAM_SCREEN_NAME = "screen_name"
        const val PARAM_SCREEN_CLASS = "screen_class"
    }
}
