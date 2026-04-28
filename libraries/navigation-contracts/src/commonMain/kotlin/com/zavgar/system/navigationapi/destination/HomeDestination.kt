package com.zavgar.system.navigationapi.destination

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector
import com.zavgar.system.navigationapi.marker.TopLevel
import com.zavgar.system.parcelable.CommonIgnoredOnParcel
import com.zavgar.system.parcelable.CommonParcelize
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_bottom_title_wallet
import com.zavgar.system.resources.home_title_history
import com.zavgar.system.resources.home_title_setting
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

object HomeDestination {

    @Serializable
    @CommonParcelize
    data object Wallet : Destination, TopLevel {

        @CommonIgnoredOnParcel
        override val icon: ImageVector = Icons.Outlined.Home

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_bottom_title_wallet
    }

    @Serializable
    @CommonParcelize
    data object Settings : Destination, TopLevel {

        @CommonIgnoredOnParcel
        override val icon: ImageVector = Icons.Outlined.Settings

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_title_setting

    }

    @Serializable
    @CommonParcelize
    data object History : Destination, TopLevel {

        // TODO Поменять на иконку истории
        @CommonIgnoredOnParcel
        override val icon: ImageVector = Icons.Outlined.WorkHistory

        @CommonIgnoredOnParcel
        override val bottomTitle: StringResource = Res.string.home_title_history
    }
}