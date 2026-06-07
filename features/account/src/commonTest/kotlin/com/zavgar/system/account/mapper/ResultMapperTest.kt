package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.utils.result.AppResult
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountResultMapperTest {

    private val profile = UserProfile(name = "John", phone = "1234567890", birthDate = LocalDate(1990, 1, 1))

    @Test
    fun `toProfileGetResult maps Success to a Success carrying the profile`() {
        val source: AppResult<UserProfile, ProfileError> = AppResult.Success(profile)

        val result = source.toProfileGetResult()

        assertTrue(result is ProfileGetResult.Success)
        assertEquals(profile, result.profile)
    }

    @Test
    fun `toProfileGetResult maps Error through the profile error mapper`() {
        val source: AppResult<UserProfile, ProfileError> = AppResult.Error(ProfileError.UserNotFound)

        val result = source.toProfileGetResult()

        assertTrue(result is ProfileGetResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found)),
            result.message,
        )
    }

    @Test
    fun `toProfileUpdateResult maps both branches`() {
        val success: AppResult<Unit, ProfileError> = AppResult.Success(Unit)
        assertEquals(ProfileUpdateResult.Success, success.toProfileUpdateResult())

        val error: AppResult<Unit, ProfileError> = AppResult.Error(ProfileError.ValidationError)
        val mapped = error.toProfileUpdateResult()
        assertTrue(mapped is ProfileUpdateResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format)),
            mapped.message,
        )
    }

    @Test
    fun `toDeleteResult maps both branches`() {
        val success: AppResult<Unit, DeleteError> = AppResult.Success(Unit)
        assertEquals(DeleteResult.Success, success.toDeleteResult())

        val error: AppResult<Unit, DeleteError> = AppResult.Error(DeleteError.ServerError)
        val mapped = error.toDeleteResult()
        assertTrue(mapped is DeleteResult.Error)
        assertEquals(SnackBarType.ERROR, mapped.message.type)
    }

    @Test
    fun `profile asSnackBarMessage maps domain-specific and AppError errors`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format)),
            ProfileError.ValidationError.asSnackBarMessage(),
        )
        assertEquals(SnackBarType.ERROR, ProfileError.NetworkError.asSnackBarMessage().type)
    }
}
