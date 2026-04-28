package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ProfileGetResult {
    data class Success(val profileResponse: ProfileResponse) : ProfileGetResult
    data class Error(val message: SnackBarMessage) : ProfileGetResult
}
