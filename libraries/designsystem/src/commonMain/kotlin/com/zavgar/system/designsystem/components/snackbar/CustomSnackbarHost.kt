package com.zavgar.system.designsystem.components.snackbar

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.ic_alert
import com.zavgar.system.resources.ic_dismiss
import com.zavgar.system.resources.ic_error
import com.zavgar.system.resources.ic_info
import com.zavgar.system.resources.ic_success
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource


private val alertColor = Color(color = 0xFFFFC10E)

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
    val indicatorColor = getSnackbarColor(visuals.type)

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
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.small)
                        .background(indicatorColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = vectorResource(getSnackbarIcon(visuals.type)),
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                ) {
                    if (!visuals.title.isNullOrEmpty()) {
                        Text(
                            text = visuals.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = indicatorColor
                        )
                    }
                    Text(
                        text = visuals.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Кнопка Action
                if (visuals.actionLabel != null) {
                    TextButton(
                        onClick = {
                            onAction?.invoke()
                            snackBarData.performAction()
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = visuals.actionLabel,
                            color = indicatorColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                if (visuals.withDismissAction) {
                    IconButton(
                        onClick = {
                            onDismiss?.invoke()
                            snackBarData.dismiss()
                        }
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_dismiss),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getSnackbarColor(type: SnackBarType): Color = when (type) {
    SnackBarType.INFO -> MaterialTheme.colorScheme.primary
    SnackBarType.SUCCESS -> MaterialTheme.colorScheme.tertiary
    SnackBarType.WARNING -> alertColor
    SnackBarType.ERROR -> MaterialTheme.colorScheme.error
}

@Composable
private fun getSnackbarIcon(type: SnackBarType): DrawableResource = when (type) {
    SnackBarType.INFO -> Res.drawable.ic_info
    SnackBarType.SUCCESS -> Res.drawable.ic_success
    SnackBarType.WARNING -> Res.drawable.ic_alert
    SnackBarType.ERROR -> Res.drawable.ic_error
}