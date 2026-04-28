package com.zavgar.system.domain.di

import com.zavgar.system.domain.logout.LogoutHandler
import com.zavgar.system.domain.usecase.ChangePasswordUseCase
import com.zavgar.system.domain.usecase.ConfirmationUseCase
import com.zavgar.system.domain.usecase.DeleteProfileUseCase
import com.zavgar.system.domain.usecase.DeleteSessionUseCase
import com.zavgar.system.domain.usecase.GetOperationsUseCase
import com.zavgar.system.domain.usecase.GetProfileUseCase
import com.zavgar.system.domain.usecase.GetSessionUseCase
import com.zavgar.system.domain.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.usecase.LoginUseCase
import com.zavgar.system.domain.usecase.LogoutUseCase
import com.zavgar.system.domain.usecase.RegisterUseCase
import com.zavgar.system.domain.usecase.ResendCodeUseCase
import com.zavgar.system.domain.usecase.ResetPasswordUseCase
import com.zavgar.system.domain.usecase.UpdateProfileUseCase
import com.zavgar.system.domain.usecase.validation.ValidateBirthDateUseCase
import com.zavgar.system.domain.usecase.validation.ValidateCodeUseCase
import com.zavgar.system.domain.usecase.validation.ValidateNameUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePasswordUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePhoneUseCase
import com.zavgar.system.domain.usecase.validation.ValidateRepeatedPasswordUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    //Loyalty repository UseCase
    factoryOf(::GetUserBalanceUseCase)
    factoryOf(::GetOperationsUseCase)

    // Data Source repository UseCase
    factoryOf(::GetSessionUseCase)
    factoryOf(::DeleteSessionUseCase)

    // User Profile UseCase
    factoryOf(::GetProfileUseCase)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::ChangePasswordUseCase)

    // Auth UseCase
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::ConfirmationUseCase)
    factoryOf(::ResendCodeUseCase)
    factoryOf(::ResetPasswordUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::DeleteProfileUseCase)
    factoryOf(::LogoutHandler)

    // Validation UseCase
    factoryOf(::ValidatePhoneUseCase)
    factoryOf(::ValidatePasswordUseCase)
    factoryOf(::ValidateBirthDateUseCase)
    factoryOf(::ValidateNameUseCase)
    factoryOf(::ValidateRepeatedPasswordUseCase)
    factoryOf(::ValidateCodeUseCase)

}