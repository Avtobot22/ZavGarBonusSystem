package com.zavgar.system.navigationapi.destination

import com.zavgar.system.navigationapi.marker.TopLevel
import com.zavgar.system.parcelable.CommonIgnoredOnParcel
import com.zavgar.system.parcelable.CommonParcelize
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.history_icon
import com.zavgar.system.resources.home_bottom_title_wallet
import com.zavgar.system.resources.home_icon
import com.zavgar.system.resources.home_title_history
import com.zavgar.system.resources.home_title_setting
import com.zavgar.system.resources.settings_icon
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

object HomeDestination {

    @Serializable
    @CommonParcelize
    data object Wallet : Destination, TopLevel {

        @CommonIgnoredOnParcel
        override val icon: DrawableResource = Res.drawable.home_icon

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_bottom_title_wallet
    }

    @Serializable
    @CommonParcelize
    data object Settings : Destination, TopLevel {

        @CommonIgnoredOnParcel
        override val icon: DrawableResource = Res.drawable.settings_icon

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_title_setting

    }

    @Serializable
    @CommonParcelize
    data object History : Destination, TopLevel {

        @CommonIgnoredOnParcel
        override val icon: DrawableResource = Res.drawable.history_icon

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_title_history
    }
}