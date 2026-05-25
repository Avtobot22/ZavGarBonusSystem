package com.zavgar.system.domain.userinfo.repository

import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.model.CachedBalance
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.utils.result.AppResult
import kotlinx.datetime.LocalDate

interface ProfileRepository {
    suspend fun getProfile(): AppResult<UserProfile, ProfileError>
    suspend fun updateProfile(name: String, birthDate: LocalDate): AppResult<Unit, ProfileError>
    suspend fun delete(): AppResult<Unit, DeleteError>
    suspend fun getBalance(): AppResult<Balance, GetBalanceError>
    suspend fun getCachedBalance(): CachedBalance?
    suspend fun getMonthlyAccruals(): AppResult<Int, MonthlyAccrualsError>
    suspend fun logout(): AppResult<Unit, LogoutError>
}
