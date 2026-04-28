package com.zavgar.system.navigationapi.provider

import androidx.navigation3.runtime.EntryProviderScope
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination

interface NavGraph {

    val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit
}