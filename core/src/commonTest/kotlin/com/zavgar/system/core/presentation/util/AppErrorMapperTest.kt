package com.zavgar.system.core.presentation.util

import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.result.AppError
import kotlin.test.Test
import kotlin.test.assertEquals

class AppErrorMapperTest {

    private val tooManyRequest = object : AppError.TooManyRequest {}
    private val server = object : AppError.Server {}
    private val network = object : AppError.Network {}
    private val unknown = object : AppError.Unknown {
        override val message: String = "unexpected"
    }

    @Test
    fun `maps TooManyRequest to a warning snackbar`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_too_many_requests)),
            tooManyRequest.asSnackBarMessage(),
        )
    }

    @Test
    fun `maps Server to an error snackbar`() {
        assertEquals(
            SnackBarMessage.error(UiText.Resource(Res.string.error_server_error)),
            server.asSnackBarMessage(),
        )
    }

    @Test
    fun `maps Network to an error snackbar`() {
        assertEquals(
            SnackBarMessage.error(UiText.Resource(Res.string.error_network_error)),
            network.asSnackBarMessage(),
        )
    }

    @Test
    fun `maps Unknown to an error snackbar`() {
        assertEquals(
            SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
            unknown.asSnackBarMessage(),
        )
    }

    @Test
    fun `mapAppError delegates to asSnackBarMessage`() {
        assertEquals(server.asSnackBarMessage(), mapAppError(server))
    }
}
