package com.zavgar.system.navigationapi.destination

import com.zavgar.system.navigationapi.marker.EdgeToEdge
import com.zavgar.system.parcelable.CommonParcelize
import kotlinx.serialization.Serializable

/**
 * Onboarding destination - shown once on first launch, between Splash and Login.
 */
@Serializable
@CommonParcelize
data object OnboardingDestination : Destination, EdgeToEdge
