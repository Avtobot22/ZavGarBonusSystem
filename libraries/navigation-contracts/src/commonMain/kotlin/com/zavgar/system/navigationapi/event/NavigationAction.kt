package com.zavgar.system.navigationapi.event

/**
 * Marker interface for navigation action type.
 * Определяет как должен обрабатываться переход.
 */
sealed interface NavigationAction

/**
 * Replace current destination (removes current, adds new).
 * Используется для Splash → Login/Wallet.
 */
interface ReplaceNavigation : NavigationAction

/**
 * Clear all stacks and navigate to destination.
 * Используется для Logout → Login.
 */
interface ClearAndNavigate : NavigationAction

/**
 * Clear all and navigate to TopLevel destination.
 * Используется для Auth success → Wallet.
 */
interface ClearAndNavigateToTopLevel : NavigationAction
