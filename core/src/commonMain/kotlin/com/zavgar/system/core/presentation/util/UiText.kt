package com.zavgar.system.core.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {

    data class DynamicString(val value: String) : UiText

    data class Resource(
        val res: StringResource,
        val args: List<Any> = emptyList()
    ) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is Resource -> stringResource(res, *args.toTypedArray())
        }
    }

    suspend fun suspendAsString(): String {
        return when (this) {
            is DynamicString -> value
            is Resource -> getString(res, *args.toTypedArray())
        }
    }

    companion object {

        operator fun invoke(res: StringResource, vararg args: Any) = Resource(res, args.toList())
    }
}