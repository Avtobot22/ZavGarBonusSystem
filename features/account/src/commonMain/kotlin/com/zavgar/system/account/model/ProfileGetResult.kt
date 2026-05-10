package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.domain.userinfo.model.UserProfile

sealed interface ProfileGetResult {
    data class Success(val profile: UserProfile) : ProfileGetResult
    data class Error(val message: SnackBarMessage) : ProfileGetResult
}
