package com.zavgar.system.designsystem.components.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.ic_dismiss
import com.zavgar.system.resources.ic_error_filled
import com.zavgar.system.resources.ic_info_filled
import com.zavgar.system.resources.ic_success_filled
import com.zavgar.system.resources.ic_warning_filled
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

// ─── Палитра типов снэкбара (стиль «Subtle» из SnackBarDesign) ──────────
// Фиксированные брендовые цвета, одинаковые в светлой и тёмной теме.
private data class SnackPalette(val accent: Color, val soft: Color)

private val SnackInfo = SnackPalette(accent = Color(0xFF2A6FDB), soft = Color(0x1F2A6FDB))
private val SnackSuccess = SnackPalette(accent = Color(0xFF1F8A5B), soft = Color(0x1F1F8A5B))
private val SnackWarning = SnackPalette(accent = Color(0xFFE89412), soft = Color(0x24E89412))
private val SnackError = SnackPalette(accent = Color(0xFFE64646), soft = Color(0x1FE64646))

private val SnackbarShape = RoundedCornerShape(16.dp)

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
        snackbar = { snackbarData ->
            val visuals = snackbarData.visuals as? CustomSnackbarVisuals

            if (visuals != null) {
                SnackBar(
                    snackBarData = snackbarData,
                    visuals = visuals,
                    onAction = onAction
                )
            } else {
                Snackbar(snackbarData = snackbarData)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SnackBar(
    snackBarData: SnackbarData,
    visuals: CustomSnackbarVisuals,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val palette = getSnackbarPalette(visuals.type)

    val swipeToDismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeToDismissState.currentValue) {
        if (swipeToDismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onDismiss?.invoke()
            snackBarData.dismiss()
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissState,
        backgroundContent = {}
    ) {
        Surface(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = SnackbarShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.Top
                ) {
                    // Цветная полоса-акцент слева
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(palette.accent)
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Иконка в круге с мягкой заливкой
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(palette.soft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vectorResource(getSnackbarIcon(visuals.type)),
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val hasTitle = !visuals.title.isNullOrEmpty()

                            Text(
                                text = if (hasTitle) visuals.title.orEmpty() else visuals.message,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.1).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (hasTitle) {
                                Text(
                                    text = visuals.message,
                                    modifier = Modifier.padding(top = 3.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.5.sp,
                                        lineHeight = 19.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (visuals.actionLabel != null) {
                                SnackbarActionButton(
                                    label = visuals.actionLabel,
                                    accent = palette.accent,
                                    onClick = {
                                        onAction?.invoke()
                                        snackBarData.performAction()
                                    }
                                )
                            }
                        }

                        if (visuals.withDismissAction) {
                            Spacer(modifier = Modifier.width(8.dp))
                            SnackbarCloseButton(
                                onClick = {
                                    onDismiss?.invoke()
                                    snackBarData.dismiss()
                                }
                            )
                        }
                    }
                }

                // Progress-бар оставшегося времени показа
                val durationMillis = visuals.duration.toMillisOrNull()
                if (durationMillis != null) {
                    SnackbarProgressBar(
                        accent = palette.accent,
                        durationMillis = durationMillis,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackbarActionButton(
    label: String,
    accent: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .clip(shape)
            .border(width = 1.5.dp, color = accent, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = accent,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.1).sp
            )
        )
    }
}

@Composable
private fun SnackbarCloseButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_dismiss),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SnackbarProgressBar(
    accent: Color,
    durationMillis: Int,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.value)
                .fillMaxHeight()
                .background(accent)
        )
    }
}

private fun SnackbarDuration.toMillisOrNull(): Int? = when (this) {
    SnackbarDuration.Short -> 4000
    SnackbarDuration.Long -> 10000
    SnackbarDuration.Indefinite -> null
}

private fun getSnackbarPalette(type: SnackBarType): SnackPalette = when (type) {
    SnackBarType.INFO -> SnackInfo
    SnackBarType.SUCCESS -> SnackSuccess
    SnackBarType.WARNING -> SnackWarning
    SnackBarType.ERROR -> SnackError
}

private fun getSnackbarIcon(type: SnackBarType): DrawableResource = when (type) {
    SnackBarType.INFO -> Res.drawable.ic_info_filled
    SnackBarType.SUCCESS -> Res.drawable.ic_success_filled
    SnackBarType.WARNING -> Res.drawable.ic_warning_filled
    SnackBarType.ERROR -> Res.drawable.ic_error_filled
}
