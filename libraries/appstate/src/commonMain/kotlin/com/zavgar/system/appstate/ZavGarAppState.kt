package com.zavgar.system.appstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.zavgar.system.navigationapi.controller.NavBackStack
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.SplashDestination

/**
 * ZavGar App state.
 */
@Stable
data class ZavGarAppState(val navBackStack: NavBackStack<Destination>)

/**
 * Function to remember a [ZavGarAppState].
 *
 * The [NavBackStack] is stored via [rememberSaveable] with a custom [NavBackStack.saver],
 * so that navigation state survives configuration changes (e.g. screen rotation on Android)
 * and process death.
 *
 * @param navBackStack the navigation back stack
 */
@Composable
fun rememberZavGarAppState(
    navBackStack: NavBackStack<Destination> = rememberSaveable(
        saver = NavBackStack.saver(SplashDestination),
    ) {
        NavBackStack(SplashDestination)
    },
): ZavGarAppState = remember(navBackStack) { ZavGarAppState(navBackStack) }