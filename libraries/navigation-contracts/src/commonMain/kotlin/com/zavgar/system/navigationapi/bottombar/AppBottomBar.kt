package com.zavgar.system.navigationapi.bottombar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zavgar.system.navigationapi.marker.TopLevel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AppBottomBar(
    items: ImmutableList<TopLevel>,
    currentSection: TopLevel,
    setCurrentSection: (TopLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    Surface(
        modifier = modifier,
        shape = barShape,
        shadowElevation = 8.dp,
    ) {
        NavigationBar(
            modifier = Modifier
                .navigationBarsPadding()
                .height(60.dp),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(),
        ) {
            items.forEach { item ->
                val selected = item == currentSection
                val title = item.bottomTitle
                NavigationBarItem(
                    selected = selected,
                    onClick = { setCurrentSection(item) },
                    icon = {
                        Icon(
                            imageVector = vectorResource(item.icon),
                            contentDescription = stringResource(title),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(title),
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
