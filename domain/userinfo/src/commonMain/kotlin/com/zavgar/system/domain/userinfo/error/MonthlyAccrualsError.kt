package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface MonthlyAccrualsError {
    data object NetworkError : MonthlyAccrualsError, AppError.Network
    data object ServerError : MonthlyAccrualsError, AppError.Server
    data class UnknownError(override val message: String) : MonthlyAccrualsError, AppError.Unknown
}
