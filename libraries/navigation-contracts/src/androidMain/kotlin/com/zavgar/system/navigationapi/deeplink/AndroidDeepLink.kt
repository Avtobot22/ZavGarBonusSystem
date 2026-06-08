package com.zavgar.system.navigationapi.deeplink

import android.content.Intent
import androidx.core.net.toUri
import com.zavgar.system.navigationapi.destination.Destination

/**
 * Destinations specifically for the Android platform since it requires [Intent] to navigate between
 * screens in some flows, such as notifications and widgets.
 */
object AndroidDeepLink {

    /**
     * Returns the [Intent] to the home screen.
     */
    fun homeIntent(): Intent = Intent(Intent.ACTION_VIEW, Destination.URI.toUri())
}
