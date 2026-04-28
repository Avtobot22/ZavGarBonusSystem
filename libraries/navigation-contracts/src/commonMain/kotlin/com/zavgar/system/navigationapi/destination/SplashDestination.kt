package com.zavgar.system.navigationapi.destination

import com.zavgar.system.navigationapi.marker.EdgeToEdge
import com.zavgar.system.parcelable.CommonParcelize
import kotlinx.serialization.Serializable

/**
 * Splash destination - entry point of the app.
 * Determines whether to navigate to Login or Wallet based on auth state.
 */
@Serializable
@CommonParcelize
data object SplashDestination : Destination, EdgeToEdge
