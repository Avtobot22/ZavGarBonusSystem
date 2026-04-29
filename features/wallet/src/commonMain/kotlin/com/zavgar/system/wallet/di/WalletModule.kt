package com.zavgar.system.wallet.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.wallet.domain.usecase.GetUserBalanceUseCase
import com.zavgar.system.wallet.navigation.WalletNavGraph
import com.zavgar.system.wallet.presentation.WalletViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val walletModule = module {
    factoryOf(::GetUserBalanceUseCase)
    viewModelOf(::WalletViewModel)

    factoryOf(::WalletNavGraph) bind NavGraph::class

}