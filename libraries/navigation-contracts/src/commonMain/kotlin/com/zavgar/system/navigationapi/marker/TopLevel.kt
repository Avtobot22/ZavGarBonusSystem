package com.zavgar.system.navigationapi.marker

import com.zavgar.system.parcelable.CommonParcelable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Marker interface to indicate that the destination is a top-level destination, containing its own
 * stack.
 */
interface TopLevel : BottomBarVisible, CommonParcelable {
    /**
     * Icon of the destination to be used in the top app bar and bottom bar/navigation rail.
     */
    val icon: DrawableResource

    /**
     * Title of the destination to be used in the bottom bar/navigation rail.
     */
    val bottomTitle: StringResource
}
