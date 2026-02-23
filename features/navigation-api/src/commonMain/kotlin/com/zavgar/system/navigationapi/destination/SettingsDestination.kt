package com.zavgar.system.navigationapi.destination

import com.zavgar.system.parcelable.CommonIgnoredOnParcel
import com.zavgar.system.parcelable.CommonParcelize
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_top_title
import com.zavgar.system.resources.preferences_top_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

object SettingsDestination {

    @Serializable
    @CommonParcelize
    data object Profile : Destination

    @Serializable
    @CommonParcelize
    data object AboutApp : Destination

}