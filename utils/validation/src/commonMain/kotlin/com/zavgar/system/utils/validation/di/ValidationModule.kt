package com.zavgar.system.utils.validation.di

import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.utils.validation.ValidatePasswordUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import com.zavgar.system.utils.validation.ValidateRepeatedPasswordUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val validationModule = module {
    factoryOf(::ValidatePhoneUseCase)
    factoryOf(::ValidatePasswordUseCase)
    factoryOf(::ValidateBirthDateUseCase)
    factoryOf(::ValidateNameUseCase)
    factoryOf(::ValidateRepeatedPasswordUseCase)
    factoryOf(::ValidateCodeUseCase)
}
