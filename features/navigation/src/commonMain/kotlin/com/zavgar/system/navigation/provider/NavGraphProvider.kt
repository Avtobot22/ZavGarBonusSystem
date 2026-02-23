package com.zavgar.system.navigation.provider

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.provider.NavGraph

internal data class NavGraphProvider(
    private val navEventController: NavEventController,
    private val navGraphs: List<NavGraph>,
) {
    val navigationGraph: (Destination) -> NavEntry<Destination> = entryProvider {
        navGraphs.forEach { navGraph ->
            navGraph.navGraph(this, navEventController)
        }
    }
}
