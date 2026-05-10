package com.zavgar.system.designsystem.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.header.ZavGarOrangeHeader
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*

/**
 * Унифицированный каркас для экранов auth-флоу (Login / Register / ResetPassword / Confirmation).
 */
@Composable
fun ZavGarAuthScaffold(
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    headerTopPadding: Dp = 36.dp,
    headerBottomPadding: Dp = 34.dp,
    sheetOverlap: Dp = 18.dp,
    sheetContentPadding: PaddingValues = PaddingValues(horizontal = 28.dp, vertical = 28.dp),
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        containerColor = colors.accent,
        snackbarHost = {
            Box(Modifier.navigationBarsPadding()) { snackbarHost() }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(colors.accent)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ZavGarOrangeHeader(
                topPadding = headerTopPadding,
                bottomPadding = headerBottomPadding,
            )
            AuthSheet(
                overlap = sheetOverlap,
                sheetContentPadding = sheetContentPadding,
                content = sheetContent,
            )
        }
    }
}

/**
 * Sheet-карточка: тянется на всё оставшееся пространство (до низа экрана),
 * её верх визуально приподнят на [overlap], чтобы перекрыть оранжевую шапку
 * закруглёнными углами.
 */
@Composable
private fun ColumnScope.AuthSheet(
    overlap: Dp,
    sheetContentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val density = LocalDensity.current
    val overlapPx = with(density) { overlap.roundToPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .layout { measurable, constraints ->
                val bounded = constraints.hasBoundedHeight
                val extra = if (bounded) overlapPx else 0
                val newMax = if (bounded) constraints.maxHeight + extra else constraints.maxHeight
                val newMin = (constraints.minHeight + extra).coerceAtMost(newMax)
                val placeable = measurable.measure(
                    constraints.copy(minHeight = newMin, maxHeight = newMax)
                )
                layout(placeable.width, (placeable.height - extra).coerceAtLeast(0)) {
                    placeable.place(0, -extra)
                }
            }
            .clip(ZavGarTopSheetShape)
            .background(colors.card),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(sheetContentPadding),
            content = content,
        )
    }
}
