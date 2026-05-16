package com.zavgar.system.utils.validation.di

import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val validationModule = module {
    factoryOf(::ValidatePhoneUseCase)
    factoryOf(::ValidateBirthDateUseCase)
    factoryOf(::ValidateNameUseCase)
    factoryOf(::ValidateCodeUseCase)
}
