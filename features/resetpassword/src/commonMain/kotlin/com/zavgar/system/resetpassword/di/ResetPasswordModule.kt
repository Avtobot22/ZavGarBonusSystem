package com.zavgar.system.resetpassword.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.resetpassword.domain.usecase.ResetPasswordUseCase
import com.zavgar.system.resetpassword.navigation.ResetPasswordNavGraph
import com.zavgar.system.resetpassword.presentation.ResetPasswordViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val resetPasswordModule = module {

    factoryOf(::ResetPasswordUseCase)

    viewModelOf(::ResetPasswordViewModel)

    factoryOf(::ResetPasswordNavGraph) bind NavGraph::class
}